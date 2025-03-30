package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:data.sql")
@Slf4j
@DataJpaTest
@Import(UserJpaAdapter.class)
public class IntegrationTest {

  @Autowired
  private UserJpaAdapter adapter;

  @Test
  void test() {

    var role = RoleEntity.builder().id(1).name("role#1").build();
    var user = getUserEntity(role);

    var savedUser = adapter.save(user);
    log.info("Saved user: {}", savedUser);

    var retrievedUser = adapter.findById(savedUser.getId()).orElseThrow();
    assertNotNull(retrievedUser);
    assertEquals("user@localhost", retrievedUser.getEmail());
    assertEquals("user", retrievedUser.getName());
    assertNotNull(retrievedUser.getAddress());
    assertEquals("street#1", retrievedUser.getAddress().getStreet());
    assertEquals(retrievedUser, retrievedUser.getAddress().getUser());
    assertEquals(2, retrievedUser.getAttributes().size());
    assertEquals(retrievedUser, retrievedUser.getAttributes().get(0).getUser());
    assertEquals(retrievedUser, retrievedUser.getAttributes().get(1).getUser());
    assertEquals("key#1", retrievedUser.getAttributes().get(0).getName());
    assertEquals("val#1", retrievedUser.getAttributes().get(0).getValue());
    assertEquals("key#2", retrievedUser.getAttributes().get(1).getName());
    assertEquals("val#2", retrievedUser.getAttributes().get(1).getValue());

    // Fetch projection with nested address
    var userProjections = adapter.findUsers();
    assertEquals(1, userProjections.size());
    assertEquals("user", userProjections.getFirst().name());
    assertEquals("street#1", userProjections.getFirst().address().getStreet());
    // User is fetched as well as a part of the projection, think carefully about what you need
    assertNotNull(userProjections.getFirst().address().getUser());

    var userAttrFlatProjections = adapter.findUserAttributes();
    assertEquals(2, userAttrFlatProjections.size());
    assertEquals("user", userAttrFlatProjections.get(0).name());
    assertEquals("user", userAttrFlatProjections.get(1).name());
    assertEquals("key#1", userAttrFlatProjections.get(0).attribute().getName());
    assertEquals("val#1", userAttrFlatProjections.get(0).attribute().getValue());
    assertEquals("key#2", userAttrFlatProjections.get(1).attribute().getName());
    assertEquals("val#2", userAttrFlatProjections.get(1).attribute().getValue());

    var userAttrProjections = adapter.findAllUserProjections();
    assertEquals(1, userAttrProjections.size());
    var attrs = userAttrProjections.get(0).attributes();
    assertEquals(2, attrs.size());
    assertEquals("key#1", attrs.get(0).name());
    assertEquals("val#1", attrs.get(0).value());
    assertEquals("key#2", attrs.get(1).name());
    assertEquals("val#2", attrs.get(1).value());
  }

  private static UserEntity getUserEntity(RoleEntity role) {
    var address = new AddressEntity();
    address.setStreet("street#1");

    var attribute1 = new AttributeEntity();
    attribute1.setName("key#1");
    attribute1.setValue("val#1");
    var attribute2 = new AttributeEntity();
    attribute2.setName("key#2");
    attribute2.setValue("val#2");

    var user = new UserEntity();
    user.setEmail("user@localhost");
    user.setRole(role);
    user.setName("user");

    user.setAddress(address);
    address.setUser(user);
    attribute1.setUser(user);
    attribute2.setUser(user);
    user.setAttributes(List.of(attribute1, attribute2));
    return user;
  }

}
