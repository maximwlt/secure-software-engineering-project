package com.projektsse.backend.models;

import com.projektsse.backend.controller.dto.ApiMessage;

public record ApiMessageModel(String message) {

    public ApiMessage toDto() {
        return new ApiMessage(message);
    }
}
