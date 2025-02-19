package com.interview.dtos;

import com.interview.models.Director;

public class DirectorDTO {
    private String firstName;
    private String lastName;

    public DirectorDTO() {
    }

    public DirectorDTO(Director director) {
        this.firstName = director.getFirstName();
        this.lastName = director.getLastName();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
