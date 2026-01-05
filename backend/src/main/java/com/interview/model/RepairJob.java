package com.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "repairjob")
@EntityListeners(AuditingEntityListener.class)
public class RepairJob {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    // job name
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    // who owns the vehicle
    @NotBlank
    @Column(name = "user_id", nullable = false)
    private String userId;

    // what is being repaired
    @NotBlank
    @Column(name = "repair_description", nullable = false)
    private String repairDescription;

    // vehicle details
    @NotBlank
    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @NotBlank
    @Column(name = "make", nullable = false)
    private String make;

    @NotBlank
    @Column(name = "model", nullable = false)
    private String model;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(nullable = false)
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime lastModified;

    @Enumerated(STRING)
    private RepairStatus status;
}
