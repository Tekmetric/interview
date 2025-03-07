package com.interview.dto;

import com.interview.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long id;

    @NotBlank(message = "{error.required.user.firstName}")
    private String firstName;

    @NotBlank(message = "{error.required.user.lastName}")
    private String lastName;

    @NotBlank(message = "{error.required.user.email}")
    @Email(message = "{error.invalid.user.email}")
    private String email;

    private boolean admin;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.admin = user.isAdmin();
    }

    public UserDto(long id) {
        this.id = id;
    }
}
