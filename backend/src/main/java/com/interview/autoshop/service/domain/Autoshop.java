package com.interview.autoshop.service.domain;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Autoshop {
    Long id;
    String name;
    String address;
    String phone;
    Instant createdAt;
    Instant updatedAt;
}
