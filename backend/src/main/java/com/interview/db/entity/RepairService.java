package com.interview.db.entity;


import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for storing repair service information in the database.
 */
@Entity
@Table(name = "repair_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerName;
    
    private String customerPhone;
    
    private String vehicleMake;
    
    private String vehicleModel;
    
    private Integer vehicleYear;
    
    private String licensePlate;
    
    private String serviceDescription;
    
    private Integer odometerReading;
    
    private String status;
}
