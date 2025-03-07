package com.interview.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString(exclude = "password")
public class RegistrationDto implements Serializable {

    @NotBlank(message = "{error.required.user.firstName}")
    private String firstName;

    @NotBlank(message = "{error.required.user.lastName}")
    private String lastName;

    @NotBlank(message = "{error.required.user.email}")
    @Email(message = "{error.invalid.user.email}")
    private String email;

    @NotBlank(message = "{error.required.user.password}")
    @Min(value = 3, message = "{error.minLength.user.password}")
    private String password;

    private String encodedPassword;

    public RegistrationDto(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
