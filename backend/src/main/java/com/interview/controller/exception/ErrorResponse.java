package com.interview.controller.exception;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ErrorResponse {
    private int status;
    private String message;
}
