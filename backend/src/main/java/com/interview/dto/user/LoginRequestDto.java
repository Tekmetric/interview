package com.interview.dto.user;

import com.interview.dto.ApiRequestDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class LoginRequestDto extends ApiRequestDto {

    @NotBlank(message = "email.blank")
    @Email(message = "email.invalidFormat")
    private String email;

    @NotBlank(message = "password.blank")
    private String password;

    public LoginRequestDto(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public LoginRequestDto() {
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
