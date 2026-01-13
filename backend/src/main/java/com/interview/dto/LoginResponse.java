package com.interview.dto;

import com.interview.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private Role role;
    private LocalDateTime expiresAt;
}
