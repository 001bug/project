package com.apihub.sdk.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int code;
}
