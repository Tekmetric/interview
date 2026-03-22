package com.interview.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private List<ErrorResponseItem> errors = new ArrayList<>();
}
