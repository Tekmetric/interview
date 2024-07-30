package com.interview.autoshop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carId", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Car car;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime creationDate;

    private String status;

    private String work;

    private LocalDateTime estimatedCompletionTime;

    private Double estimatedCharge;

    private LocalDateTime completionTime;

    private Double charge;
}
