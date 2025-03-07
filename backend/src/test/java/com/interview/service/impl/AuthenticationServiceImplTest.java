package com.interview.service.impl;

import com.interview.dto.LoginDto;
import com.interview.dto.LoginResponseDto;
import com.interview.entity.User;
import com.interview.jwt.JwtService;
import com.interview.repository.UserRepository;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;


    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private LoginDto loginDto;
    private User user;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto(CommonTestConstants.EMAIL_1, CommonTestConstants.PASSWORD);

        user = new User(
                CommonTestConstants.ID_1,
                CommonTestConstants.FIRST_NAME_1,
                CommonTestConstants.LAST_NAME_1,
                CommonTestConstants.EMAIL_1,
                CommonTestConstants.ENCODED_PASSWORD,
                false);
    }

    @Test
    void testSuccessfulAuthentication() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.getExpirationTime()).thenReturn(CommonTestConstants.JWT_EXPIRES_IN);
        when(jwtService.generateToken(any())).thenReturn(CommonTestConstants.JWT_TOKEN);
        when(userRepository.findByEmail(CommonTestConstants.EMAIL_1)).thenReturn(Optional.of(user));

        LoginResponseDto result = authenticationService.authenticate(loginDto);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUser().getEmail());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(CommonTestConstants.EMAIL_1);
    }

    @Test
    void testAuthenticationWithInvalidUsername() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginDto));
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(CommonTestConstants.EMAIL_1);
    }

    @Test
    void testAuthenticationFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginDto));
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }
}
