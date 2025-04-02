package com.interview.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data transfer object that maps the model stored in the database with the representation used by the web endpoints.
 * Having a separate entity model and an endpoint model decouples the application layers (storage versus presentation interface)
 * for better app scalability and maintanability.
 *
 * Lombok library is used to reduce boilerplate of equals, hashcode methods (getters and setters too altough they didn't seem
 * to play nice this time with IDE)
 */
@Data
@Getter @Setter
public class CustomerDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private Short birthYear;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime createdAt;

    public CustomerDTO() {}

    public CustomerDTO(Long id, String email, String firstName, String lastName, String address, Short birthYear, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthYear = birthYear;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public CustomerDTO(String email, String firstName, String lastName, String address, Short birthYear) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthYear = birthYear;
    }

}
