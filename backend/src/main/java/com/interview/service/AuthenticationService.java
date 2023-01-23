package com.interview.service;

import com.interview.config.ApplicationProperties;
import com.interview.dto.user.AuthResponseDto;
import com.interview.dto.user.LoginRequestDto;
import com.interview.dto.user.TokenResponseDto;
import com.interview.exception.BadRequestException;
import com.interview.model.Token;
import com.interview.model.TokenType;
import com.interview.model.User;
import com.interview.repository.TokenRepository;
import com.interview.repository.UserRepository;
import com.interview.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Transactional
public class AuthenticationService {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "rt_cookie";

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ApplicationProperties applicationProperties;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    public AuthenticationService(UserRepository userRepository, TokenService tokenService, ApplicationProperties applicationProperties, AuthenticationManager authenticationManager, UserService userService, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.applicationProperties = applicationProperties;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    public AuthResponseDto login(UserPrincipal userPrincipal) {
        User user = userService.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new BadRequestException("userNotFound"));
        return getAuthResponse(user);
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        UserPrincipal userPrincipal = getUserPrincipal(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return login(userPrincipal);
    }

    public Optional<Token> getRefreshToken() {
        HttpServletRequest request = Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).map(ServletRequestAttributes::getRequest).orElseThrow(IllegalStateException::new);
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies()).filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())).findFirst().flatMap(cookie -> tokenRepository.findByValueAndTokenType(cookie.getValue(), TokenType.REFRESH));
        }
        return Optional.empty();
    }

    public String createAccessToken(User user) {
        return tokenService.createJwtTokenValue(user.getId(), Duration.of(applicationProperties.getAuth().getAccessTokenExpirationMsec(), ChronoUnit.MILLIS));
    }

    public String createAccessTokenFromRefreshToken(User user, String token) {
        Optional<User> optionalUser = userService.findById(tokenService.getUserIdFromToken(token));
        if (optionalUser.isPresent() && user.getId().equals(optionalUser.get().getId())) {
            return createAccessToken(user);
        }
        throw new BadRequestException("userNotFound");
    }

    private Token createRefreshToken(User user) {
        return tokenService.createToken(user, Duration.of(applicationProperties.getAuth().getRefreshTokenExpirationMsec(), ChronoUnit.MILLIS), TokenType.REFRESH);
    }

    private void addRefreshToken(User user) {
        Token refreshToken = createRefreshToken(user);
        HttpServletResponse response = Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).map(ServletRequestAttributes::getResponse).orElseThrow(IllegalStateException::new);
        Date expires = new Date();
        expires.setTime(expires.getTime() + applicationProperties.getAuth().getRefreshTokenExpirationMsec());
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        response.setHeader("Set-Cookie", String.format("%s=%s; Expires=%s; Path=/; HttpOnly; SameSite=none; Secure", REFRESH_TOKEN_COOKIE_NAME, refreshToken.getValue(), df.format(expires)));
    }

    public void removeRefreshToken() {
        HttpServletResponse response = Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).map(ServletRequestAttributes::getResponse).orElseThrow(IllegalStateException::new);
        Date expires = new Date();
        expires.setTime(expires.getTime() + 1);
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        response.setHeader("Set-Cookie", String.format("%s=; Expires=%s; Path=/; HttpOnly; SameSite=none; Secure", REFRESH_TOKEN_COOKIE_NAME, df.format(expires)));
    }

    public void logout(User user) {
        Optional<Token> optionalRefreshToken = getRefreshToken();
        if (optionalRefreshToken.isPresent() && optionalRefreshToken.get().getUser().getId().equals(user.getId())) {
            tokenService.delete(optionalRefreshToken.get());
            removeRefreshToken();
        } else {
            throw new BadRequestException("tokenExpired");
        }

    }

    private UserPrincipal getUserPrincipal(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return (UserPrincipal) authentication.getPrincipal();
    }

    private AuthResponseDto getAuthResponse(User user) {
        String accessToken = createAccessToken(user);
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(accessToken);
        addRefreshToken(user);
        return authResponseDto;
    }

}
