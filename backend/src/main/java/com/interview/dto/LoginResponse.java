package com.interview.dto;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {}
