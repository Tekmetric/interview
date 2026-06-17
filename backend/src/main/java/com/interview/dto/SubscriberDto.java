package com.interview.dto;

public record SubscriberDto(
    Long id,
    String name,
    String email
) {
    public static SubscriberDto of(Long id, String firstName, String lastName, String email) {
        String fullName = (firstName != null && lastName != null)
            ? firstName + " " + lastName
            : (firstName != null ? firstName : (lastName != null ? lastName : "Unknown"));
        return new SubscriberDto(id, fullName, email);
    }
}