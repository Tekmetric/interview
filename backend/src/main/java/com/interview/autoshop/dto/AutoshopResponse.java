package com.interview.autoshop.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AutoshopResponse {
    Long id;
    String name;
    String address;
    String phone;
    Instant createdAt;
    Instant updatedAt;
}
