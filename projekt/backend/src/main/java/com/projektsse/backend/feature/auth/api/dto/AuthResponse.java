package com.projektsse.backend.feature.auth.api.dto;

/**
 * DTO for authentication response, containing the access token.
 * @param accessToken the JWT access token issued upon successful authentication
 */
public record AuthResponse(
        String accessToken
) { }
