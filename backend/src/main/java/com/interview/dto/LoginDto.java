package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto implements Serializable {
    @NotBlank(message = "{error.required.login.username}")
    @Email(message = "{error.invalid.login.email}")
    private String username;

    @NotBlank(message = "{error.required.login.password}")
    private String password;
}
