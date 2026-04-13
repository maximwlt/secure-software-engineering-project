package com.projektsse.backend.shared.exceptions;

public class UnauthorizedExceptionCustom extends RuntimeException {
    public UnauthorizedExceptionCustom(String message) {
        super(message);
    }
}
