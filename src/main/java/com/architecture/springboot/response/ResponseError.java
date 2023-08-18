package com.architecture.springboot.response;

public record ResponseError(String code, String message) {
    public ResponseError {
        if(code == null || message == null) {
            throw new IllegalArgumentException("code or message cannot be null, code : " + code + ", message : " + message);
        }
    }
}
