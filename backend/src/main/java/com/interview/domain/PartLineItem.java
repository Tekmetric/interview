package com.interview.domain;

import java.util.UUID;

public record PartLineItem(UUID id, String name, int quantity, UUID partNumber) {}
