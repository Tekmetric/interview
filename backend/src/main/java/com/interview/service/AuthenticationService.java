package com.interview.service;


import com.interview.dto.LoginDto;
import com.interview.dto.LoginResponseDto;
import com.interview.dto.RegistrationDto;

public interface AuthenticationService {
    LoginResponseDto authenticate(LoginDto loginDto);
    RegistrationDto register(RegistrationDto registrationDto);
}
