package com.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

public final class BookStoreExceptions {

    private BookStoreExceptions() {
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class BookNotFoundException extends RuntimeException {
        public BookNotFoundException(UUID id) {
            super("Book not found: " + id);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateIsbnException extends RuntimeException {
        public DuplicateIsbnException(String isbn) {
            super("A book with ISBN '" + isbn + "' already exists.");
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class OptimisticLockConflictException extends RuntimeException {
        public OptimisticLockConflictException(UUID id, long clientVersion, long serverVersion) {
            super(("Optimistic lock conflict on book %s: " +
                    "client sent version=%d but server has version=%d. " +
                    "Fetch the latest state and retry.")
                    .formatted(id, clientVersion, serverVersion));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidSortParameterException extends RuntimeException {
        public InvalidSortParameterException(String parameter) {
            super("Invalid sort parameter : " + parameter);
        }
    }
}