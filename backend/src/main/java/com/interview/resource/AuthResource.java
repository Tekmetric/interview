package com.interview.resource;

import static com.interview.security.JwtAuthFilter.SECRET_KEY;

import com.interview.api.AuthApi;
import com.interview.dto.auth.AuthRequestDTO;
import com.interview.dto.auth.AuthResponseDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
public class AuthResource implements AuthApi {

  private final AuthenticationManager authenticationManager;

  @Override
  public ResponseEntity<AuthResponseDTO> login(final AuthRequestDTO request) {
    final Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    final Object user = auth.getPrincipal();
    if (user instanceof UserDetails userDetails) {
      final String token =
          Jwts.builder()
              .setSubject(userDetails.getUsername())
              .claim(
                  "authorities",
                  userDetails.getAuthorities().stream()
                      .map(GrantedAuthority::getAuthority)
                      .toList())
              .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
              .compact();

      final AuthResponseDTO response = AuthResponseDTO.builder().token(token).build();

      return ResponseEntity.ok(response);
    }

    log.warn("Authentication failed for user: {}", request.getUsername());
    throw new IllegalStateException("Authentication failed");
  }
}
