package com.interview.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Represents a customer entity in the system. Each customer can have multiple vehicles and is
 * identified by their email.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "vehicles") // Exclude to avoid lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  @Column(nullable = false, length = 100)
  private String name;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email is required")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Size(max = 255, message = "Address must not exceed 255 characters")
  @Column // length = 255
  private String address;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<VehicleEntity> vehicles = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(name = "updated_date", nullable = false)
  private LocalDateTime updatedDate;

  // Constructor with required fields only
  public CustomerEntity(String name, String email) {
    this.name = name;
    this.email = email;
    this.vehicles = new ArrayList<>();
  }

  // Helper methods for bidirectional relationship
  public void addVehicle(VehicleEntity vehicle) {
    vehicles.add(vehicle);
    vehicle.setCustomer(this);
  }

  public void removeVehicle(VehicleEntity vehicle) {
    vehicles.remove(vehicle);
    vehicle.setCustomer(null);
  }

}
