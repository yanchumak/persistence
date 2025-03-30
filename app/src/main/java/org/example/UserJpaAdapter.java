package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter {

  private final EntityManager entityManager;

  private final UserRepository repository;

  public List<UserProjection> findUsers() {
    return repository.findUsers();
  }

  public List<UserAttributeFlatProjection> findUserAttributes() {
    return repository.findUserAttributes();
  }

  public UserEntity save(UserEntity user) {
    return repository.saveAndFlush(user);
  }

  public Optional<UserEntity> findById(Integer id) {
    return repository.findById(id);
  }

  public List<UserAttributeProjection> findAllUserProjections() {
    var sql = """
        SELECT u.id AS user_id, u.name AS user_name,
               a.name AS attr_name, a.val AS attr_value
        FROM tb_user u
        LEFT JOIN tb_attribute a ON u.id = a.user_id
    """;

    var query = entityManager.createNativeQuery(sql, Tuple.class);

    List<Tuple> results = query.getResultList();

    return groupUserAttributes(results).stream().toList();
  }

  private Collection<UserAttributeProjection> groupUserAttributes(List<Tuple> tuples) {
    Map<Integer, UserAttributeProjection> userMap = new LinkedHashMap<>();

    for (var tuple : tuples) {
      var userId = tuple.get("user_id", Integer.class);
      var userName = tuple.get("user_name", String.class);
      var attrName = tuple.get("attr_name", String.class);
      var attrValue = tuple.get("attr_value", String.class);

      var userBuilder = userMap.computeIfAbsent(userId, id ->
          UserAttributeProjection.builder()
              .id(String.valueOf(id))
              .name(userName)
              .attributes(new ArrayList<>()).build()
      );

      userBuilder.attributes()
          .add(AttributeProjection.builder().name(attrName).value(attrValue).build());
    }

    return userMap.values();
  }
}
