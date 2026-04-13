package com.projektsse.backend.shared.models;

import com.projektsse.backend.shared.api.ApiMessage;

public record ApiMessageModel(String message) {

    public ApiMessage toDto() {
        return new ApiMessage(message);
    }
}
