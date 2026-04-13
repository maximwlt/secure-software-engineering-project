package com.projektsse.backend.shared.api;

/**
 * A simple DTO for API responses that contain a message. This can be used for various endpoints to return a standardized message format.
 * @param message the message to be returned in the API response.
 */
public record ApiMessage(String message) {
}
