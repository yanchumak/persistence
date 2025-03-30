package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Table(name = "tb_address")
@Builder
public class AddressEntity {

  @Id
  private Integer userId;

  @OneToOne
  @MapsId
  // Tells Hibernate to use the user_id column as the primary key
  // It works with 1-2-1 relationships only
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "street")
  private String street;
}

