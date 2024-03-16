package com.interview.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ApiException extends RuntimeException {
    final HttpStatus status;
    final String code;
    final String message;

    public static ApiException notFound(String domain, String id) {
        return new ApiException(
                HttpStatus.NOT_FOUND,
                "not-found-" + domain,
                "[ " + id + " ] " + domain + " Not Found"
        );
    }

    public static ApiException forbidden(String domain) {
        return new ApiException(
                HttpStatus.FORBIDDEN,
                "access_denied",
                "[ " + domain + " ] " + "You don't have enough permission"
        );
    }

    public static ApiException userExists() {
        return new ApiException(
                HttpStatus.CONFLICT,
                "User exists",
                "User already exists, please try sign in"
        );
    }

    public static ApiException badCredentials() {
        return new ApiException(
                HttpStatus.BAD_REQUEST,
                "Bad Credentials",
                "Please check your credentials"
        );
    }
}
