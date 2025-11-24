package com.interview.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
public class UserDto {
	@NotNull
    private Long userId;
	@NotBlank
    private String userName;
	@Email
    private String userEmail;
	@NotBlank
    private String userPhone;

}
