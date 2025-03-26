package com.interview.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object that maps the model stored in the database with the representation used by the web endpoints.
 * Having a separate entity model and an endpoint model decouples the application layers (storage versus presentation interface)
 * for better app scalability and maintanability.
 *
 * Lombok library is used to reduce boilerplate of equals, hashcode methods (getters and setters too altough they didn't seem
 * to play nice this time with IDE)
 */
@Data
public class CustomerDTO {
    private Long id;
    private String email;

    public CustomerDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
