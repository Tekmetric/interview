package com.interview.model.dto;

import com.interview.model.enums.ServiceOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderDTO {
    private Long id;

    @NotBlank(message = "description is required")
    private String description;
    private LocalDateTime createdAt;
    private ServiceOrderStatus status;

    public ServiceOrderDTO(String description, LocalDateTime createdAt, ServiceOrderStatus status) {
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
    }
}