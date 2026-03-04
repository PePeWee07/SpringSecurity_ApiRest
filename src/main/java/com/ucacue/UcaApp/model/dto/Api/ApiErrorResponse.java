package com.ucacue.UcaApp.model.dto.Api;

import java.util.List;

public class ApiErrorResponse {

    private int status;
    private String message;
    private List<ApiError> errors;

    public ApiErrorResponse(int status, String message, List<ApiError> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ApiError> getErrors() {
        return errors;
    }
}