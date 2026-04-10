package com.projektsse.backend.models.opa;

public record OpaInput(
        OpaUser user,
        OpaResource resource,
        String action
) {
}
