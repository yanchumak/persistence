package org.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@DataJpaTest
@Slf4j
public class SaveDeletedEntityTest {

  static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4.4")
      .withDatabaseName("db")
      .withInitScript("save_deleted_entity.sql");

  static {
    try {
      Startables.deepStart(MYSQL).get(1, TimeUnit.MINUTES);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
  }

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private PlatformTransactionManager tx;

  @Test
  void saveDeletedEntityTest() throws InterruptedException {
    var latch = new CountDownLatch(1);

    // T1: Emulate a transaction using JPA session
    var t1 = new Thread(() -> {
      new TransactionTemplate(tx).execute(status -> {
        // Begin a new JPA session
        var order = orderRepository.findById(1).orElseThrow();
        log.info("T1 read: {}", order);
        sleep(1000);

        orderRepository.delete(order);
        log.info("T1 deleted");

        return null;
      });
      latch.countDown(); // allow T2 to proceed
    });

    // T2: Another thread using a separate transaction and JPA session
    var t2 = new Thread(() -> {
      new TransactionTemplate(tx).execute(status -> {
        try {
          // Begin a new JPA session in a new transaction
          var order = orderRepository.findById(1).orElseThrow();
          log.info("T2 read: {}", order);

          latch.await(); // wait for T1 to finish

          // T2 updates the order entity in the same JPA session
          order.setName("T2 updated");
          orderRepository.save(order);
          log.info("T2 updated the order");

        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      });

    });

    t1.start();
    t2.start();

    t1.join();
    t2.join();

    // Final state: Check the DB for the final state of the order
    log.info("Final DB state: {}", orderRepository.findAll());
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
