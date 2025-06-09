package com.interview.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "owner",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_owner_personal_number", columnNames = "personal_number"))
@Getter
@Setter
@NoArgsConstructor
@ToString
// https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
@EqualsAndHashCode(of = "personalNumber")
@EntityListeners(AuditingEntityListener.class)
public class Owner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, name = "id")
  private Long id;

  @Column(nullable = false, name = "name")
  private String name;

  // This field is stored encrypted using AES encryption
  @Column(nullable = false, unique = true, name = "personal_number")
  @Convert(converter = PersonalNumberAesEncryptor.class)
  @NaturalId
  private String personalNumber;

  @Column(nullable = false, name = "birth_date")
  private Instant birthDate;

  @Column(nullable = false, name = "address")
  private String address;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Car> cars;

  // Version field for optimistic locking
  @Version private Long version;

  // Auditing fields
  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  public void addCar(final Car car) {
    if (cars == null) {
      cars = new HashSet<>();
    }
    cars.add(car);
    car.setOwner(this);
  }
}
