package com.interview.dto;

/**
 * Optional filter parameters for listing vehicles. All fields may be null (filter absent).
 */
public record VehicleFilter(String make, String model, Integer year) {
}
