package com.projektsse.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * Global exception handler for the application. It catches specific exceptions thrown by the controllers and services
 * and returns appropriate HTTP responses with error details using the Problem Detail format (RFC 7807) for better error reporting.
 * It contains manually thrown exceptions and validation exceptions, ensuring that clients receive consistent and informative error messages for various failure scenarios.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Illegal Argument");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Unauthorized");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> {
                    assert fe.getDefaultMessage() != null;
                    return Map.of(
                            "field",   fe.getField(),
                            "message", fe.getDefaultMessage()
                    );
                })
                .toList();
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error");
        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(cv -> Map.of(
                        "field",   extractFieldNameFromConstraintViolation(cv),
                        "message", cv.getMessage()
                ))
                .collect(Collectors.toList());
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    private String extractFieldNameFromConstraintViolation(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        int lastDotIndex = propertyPath.lastIndexOf('.');
        return lastDotIndex != -1 ? propertyPath.substring(lastDotIndex + 1) : propertyPath;
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ProblemDetail handleNoteNotFoundException(NoteNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Note Not Found");
        return problemDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("User Not Found");
        return problemDetail;
    }

    @ExceptionHandler(UnauthorizedExceptionCustom.class)
    public ProblemDetail handleUnauthorizedExceptionCustom(UnauthorizedExceptionCustom ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Unauthorized");
        return problemDetail;
    }

    /**
     * Handler for expired, invalid or non-existing password reset tokens.
     * The response includes a detailed error message and a 404 status code, indicating that the requested resource (valid token) was not found.
     * The error message should be kept constant to avoid giving potential attackers clues about the validity of tokens.
     * @param ex the WrongTokenException thrown when a password reset token is invalid, expired, or already used
     * @return ResponseEntity with error details and 404 status code
     */
    @ExceptionHandler(WrongTokenException.class)
    public ProblemDetail handleErrorPWReset(WrongTokenException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Password-Reset failed");
        problemDetail.setInstance(URI.create("/api/auth/reset-password"));
        return problemDetail;
    }

    @ExceptionHandler(VerificationFailedException.class)
    public ProblemDetail handleInvalidLinkException(VerificationFailedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Verification failed");
        problemDetail.setInstance(URI.create("/api/auth/verify-email"));
        return problemDetail;
    }


    @ExceptionHandler(StaleObjectStateException.class)
    public ProblemDetail handleStaleObjectStateException() {
        log.error("StaleObjectStateException: Optimistic locking failure detected. The resource has been modified by another process.");
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Conflict");
        problemDetail.setDetail("The resource you are trying to modify has been changed by another process. Please refresh and try again.");
        return problemDetail;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        problemDetail.setTitle("Unsupported Media Type");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        problemDetail.setTitle("Method Not Allowed");
        problemDetail.setDetail(String.format("HTTP method '%s' is not supported for this endpoint.", ex.getMethod()));
        return problemDetail;
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ProblemDetail handleWeakPasswordException(WeakPasswordException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Password does not meet security requirements");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(OpaEvaluationException.class)
    public ProblemDetail handleOPAException(OpaEvaluationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while evaluating access control policies. Please try again later.");
        problemDetail.setTitle("Access Control Evaluation Error");
        log.error("OPAException: An error occurred while evaluating access control policies. Message: {}", ex.getMessage(), ex);
        return problemDetail;
    }
}
