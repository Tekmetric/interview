package com.interview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
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
 * Represents a repair order entity in the system. Each repair order is associated with a vehicle
 * and contains details about the repair work.
 */
@Entity
@Table(name = "repair_orders")
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "vehicle") // Exclude to avoid lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RepairOrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vehicle_id", nullable = false)
  @NotNull(message = "Vehicle is required")
  private VehicleEntity vehicle;

  @NotBlank(message = "Description is required")
  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  @Column(nullable = false, length = 1000)
  private String description;

  @Enumerated(EnumType.ORDINAL)
  @NotNull(message = "Status is required")
  @Column(nullable = false)
  private RepairOrderStatus status;

  @CreatedDate
  @Column(name = "created_date", nullable = false, updatable = false)
  private OffsetDateTime createdDate;

  @LastModifiedDate
  @Column(name = "updated_date", nullable = false)
  private OffsetDateTime updatedDate;

}
