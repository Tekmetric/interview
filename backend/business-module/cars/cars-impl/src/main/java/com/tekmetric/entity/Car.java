package com.tekmetric.entity;

import jakarta.persistence.*;
import java.time.Year;
import java.util.Date;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {
  @Id @GeneratedValue private UUID id;

  private String make;

  private String model;

  // integer in H2
  private Year manufactureYear;

  private String color;

  private UUID ownerId;

  @CreationTimestamp
  @Column(updatable = false)
  private Date createdDate;

  @UpdateTimestamp private Date updatedDate;
}
