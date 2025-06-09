package com.interview.dto.car;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "CarUpdateRequestDTO", description = "Request payload for updating a new car")
@Data
@NoArgsConstructor
// @AllArgsConstructor
@Builder(toBuilder = true)
public class CarUpdateRequestDTO {}
