package com.projektsse.backend.exceptions;

public class OpaEvaluationException extends RuntimeException {
    public OpaEvaluationException(Throwable cause) {
        super("OPA evaluation failed", cause);
    }
}
