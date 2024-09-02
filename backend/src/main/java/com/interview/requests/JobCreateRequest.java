package com.interview.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobCreateRequest {
    @JsonProperty("car_id")
    private Long carId;

    @JsonProperty("service_id")
    private Long serviceId;
}
