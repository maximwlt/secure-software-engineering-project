package com.projektsse.backend.exceptions;

public class UnauthorizedExceptionCustom extends RuntimeException {
    public UnauthorizedExceptionCustom(String message) {
        super(message);
    }
}
