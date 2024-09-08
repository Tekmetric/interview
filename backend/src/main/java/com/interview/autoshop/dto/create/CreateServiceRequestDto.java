package com.interview.autoshop.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateServiceRequestDto {

    @NotNull
    private Long carId;

    private LocalDateTime creationDate;

    private String status;

    private String work;

    private LocalDateTime estimatedCompletionTime;

    private Double estimatedCharge;

    private LocalDateTime completionTime;

    private Double charge;
}
