package com.interview.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Represents a vehicle entity in the system. Each vehicle is associated with a customer and can
 * have multiple repair orders.
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"customer", "repairOrders"}) // Exclude to avoid lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VehicleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  @NotNull(message = "Customer is required")
  private CustomerEntity customer;

  @NotBlank(message = "Make is required")
  @Size(max = 50, message = "Make must not exceed 50 characters")
  @Column(nullable = false, length = 50)
  private String make;

  @NotBlank(message = "Model is required")
  @Size(max = 50, message = "Model must not exceed 50 characters")
  @Column(nullable = false, length = 50)
  private String model;

  @NotNull(message = "Year is required")
  @Min(value = 1900, message = "Year must be after 1900")
  @Column(nullable = false)
  private Integer year;

  @NotBlank(message = "License plate is required")
  @Size(max = 20, message = "License plate must not exceed 20 characters")
  @Column(name = "license_plate", nullable = false, unique = true, length = 20)
  private String licensePlate;

  @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<RepairOrderEntity> repairOrders = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(name = "updated_date", nullable = false)
  private LocalDateTime updatedDate;

  // Constructor with required fields only
  public VehicleEntity(CustomerEntity customer, String make, String model, Integer year,
      String licensePlate) {
    this.customer = customer;
    this.make = make;
    this.model = model;
    this.year = year;
    this.licensePlate = licensePlate;
    this.repairOrders = new ArrayList<>();
  }

  // Helper methods for bidirectional relationship
  public void addRepairOrder(RepairOrderEntity repairOrder) {
    repairOrders.add(repairOrder);
    repairOrder.setVehicle(this);
  }

  public void removeRepairOrder(RepairOrderEntity repairOrder) {
    repairOrders.remove(repairOrder);
    repairOrder.setVehicle(null);
  }

}
