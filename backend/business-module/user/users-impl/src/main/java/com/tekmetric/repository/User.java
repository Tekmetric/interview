package com.tekmetric.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id @GeneratedValue private UUID id;

  @NotNull private String firstName;
  @NotNull private String lastName;

  private String email;

  private LocalDate birthDate;

  @CreationTimestamp
  @Column(updatable = false)
  private Date createdDate;

  @UpdateTimestamp private Date updatedDate;
}
