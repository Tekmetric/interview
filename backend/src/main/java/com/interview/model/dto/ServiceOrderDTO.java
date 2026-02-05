package com.interview.model.dto;

import com.interview.model.enums.ServiceOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderDTO {
    private Long id;
    private String description;
    private LocalDateTime createdAt;
    private ServiceOrderStatus status;
    public  ServiceOrderDTO(String description, LocalDateTime createdAt, ServiceOrderStatus status) {
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
    }
}