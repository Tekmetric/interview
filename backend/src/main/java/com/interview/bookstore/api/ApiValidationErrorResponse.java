package com.interview.bookstore.api;

import java.util.List;

public record ApiValidationErrorResponse(String reason, List<ApiValidationError> validations) { }
