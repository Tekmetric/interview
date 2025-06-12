package com.interview.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String SECRET_KEY = "super-secret-key-which-is-at-least-256-bits";

  private final JwtParser parser =
      Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build();

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String header = request.getHeader(AUTHORIZATION_HEADER);
    if (!isBearerToken(header)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String token = header.substring(7);
      setAuthentication(token);
    } catch (JwtException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isBearerToken(final String header) {
    return header != null && header.startsWith("Bearer ");
  }

  private void setAuthentication(final String token) {
    final Claims claims = parser.parseClaimsJws(token).getBody();
    final String username = claims.getSubject();
    final List<?> rawAuthorities = claims.get("authorities", List.class);
    final List<SimpleGrantedAuthority> authorities =
        Optional.ofNullable(rawAuthorities).orElse(List.of()).stream()
            .map(Object::toString)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    final Authentication auth =
        new UsernamePasswordAuthenticationToken(username, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
