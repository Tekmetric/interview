package com.interview._infrastructure.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class CustomError {

    private String timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public CustomError(String message, HttpStatus error, String path) {
        this.status = error.value();
        this.error = error.getReasonPhrase();
        this.message = message;
        this.timeStamp = Instant.now().toString();
        this.path = path;
    }
}
