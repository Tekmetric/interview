package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "owner",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_owner_personal_number", columnNames = "personal_number"))
@Data
@EntityListeners(AuditingEntityListener.class)
public class Owner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, name = "id")
  private Long id;

  @Column(nullable = false, name = "name")
  private String name;

  @Column(nullable = false, unique = true, name = "personal_number")
  private String personalNumber;

  @Column(nullable = false, name = "birth_date")
  private Instant birthDate;

  @Column(nullable = false, name = "address")
  private String address;

  @Version private Long version;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}
