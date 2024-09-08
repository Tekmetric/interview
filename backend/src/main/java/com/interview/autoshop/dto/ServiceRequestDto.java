package com.interview.autoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequestDto {

    private Long id;

    private CarDto car;

    private LocalDateTime creationDate;

    private String status;

    private String work;

    private LocalDateTime estimatedCompletionTime;

    private Double estimatedCharge;

    private LocalDateTime completionTime;

    private Double charge;
}
