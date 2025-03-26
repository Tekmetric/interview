package com.interview.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
