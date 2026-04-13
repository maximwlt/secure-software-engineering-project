package com.projektsse.backend.shared.exceptions;

public class WrongTokenException extends RuntimeException {
    public WrongTokenException(String message) {
        super(message);
    }
}
