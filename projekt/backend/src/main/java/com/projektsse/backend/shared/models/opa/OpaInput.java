package com.projektsse.backend.shared.models.opa;

public record OpaInput(
        OpaUser user,
        OpaResource resource,
        String action
) {
}
