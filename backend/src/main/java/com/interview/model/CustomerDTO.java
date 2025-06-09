package com.interview.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @Email(message = "Email should be valid format.")
    private String email;
    @Size(min = 2, max = 50, message = "First Name should be between 2 and 50 characters")
    private String firstName;
    @Size(min = 2, max = 50, message = "First Name should be between 2 and 50 characters")
    private String lastName;
    private String address;
    @Min(value = 1900)
    @Max(value = 2025)
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
