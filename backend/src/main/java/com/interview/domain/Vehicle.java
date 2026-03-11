package com.interview.domain;

import java.util.UUID;

public record Vehicle(UUID id, Vin vin, UUID customerId) {}
