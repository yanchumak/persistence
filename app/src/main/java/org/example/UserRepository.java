package org.example;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

  @Query("SELECT new org.example.UserProjection(u.name, u.address) FROM UserEntity u")
  List<UserProjection> findUsers();

  @Query(value = """
    SELECT new org.example.UserAttributeFlatProjection(u.name, a)
    FROM UserEntity u LEFT JOIN u.attributes a
    """)
  // Additional transformation required
  List<UserAttributeFlatProjection> findUserAttributes();
}
