package com.interview.bookstore.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DuplicateFieldException extends RuntimeException {

    private Class resourceType;
    private String fieldName;

}
