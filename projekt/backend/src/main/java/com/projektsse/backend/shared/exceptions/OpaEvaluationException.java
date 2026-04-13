package com.projektsse.backend.shared.exceptions;

public class OpaEvaluationException extends RuntimeException {
    public OpaEvaluationException(Throwable cause) {
        super("OPA evaluation failed", cause);
    }
}
