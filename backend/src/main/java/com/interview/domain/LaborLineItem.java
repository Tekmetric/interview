package com.interview.domain;

import java.util.UUID;

public record LaborLineItem(UUID id, String name, int quantity, UUID serviceCode) {}
