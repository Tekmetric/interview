package com.interview.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto implements Serializable {
    private UserDto user;
    private String token;
    private long expiresIn;
}
