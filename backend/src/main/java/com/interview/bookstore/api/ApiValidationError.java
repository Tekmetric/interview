package com.interview.bookstore.api;

public record ApiValidationError(String field, String reason) { }
