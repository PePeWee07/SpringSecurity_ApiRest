package com.ucacue.UcaApp.model.dto.Api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String field,
        Object rejectedValue,
        String message,
        String error) {

    public ApiError(String message, String error) {
        this(null, null, message, error);
    }
}