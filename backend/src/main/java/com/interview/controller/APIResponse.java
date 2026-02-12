package com.interview.controller;

import lombok.Data;

@Data
public class APIResponse {

    private boolean success = true;

    private String message;

    private Object data;

}
