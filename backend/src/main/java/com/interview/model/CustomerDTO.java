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
@Getter @Setter
public class CustomerDTO {
    private Long id;
    private String email;
    private String name;
    private String address;

    public CustomerDTO() {}

    public CustomerDTO(Long id, String email, String name, String address) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
    }

    public CustomerDTO(String email, String name, String address) {
        this.email = email;
        this.name = name;
        this.address = address;
    }

}
