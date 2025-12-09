package com.interview.service;

import com.interview.config.security.JwtHelper;
import com.interview.dto.login.LoginRequest;
import com.interview.dto.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final String ROLES_CLAIM_NAME = "roles";
    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        var roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        var claims = new HashMap<String, Object>();
        claims.put(ROLES_CLAIM_NAME, roles);

        String jwt = jwtHelper.generateToken(userDetails.getUsername(), claims);
        return new LoginResponse(jwt);
    }
}
