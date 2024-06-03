package com.ucacue.UcaApp.web.response.TokenError;

import java.util.List;

public class ApiTokenErrorResponse {

    private int code;
    private String status;
    private List<TokenErrorDetail> errors;
    private String message;

    // Constructor for error
    public ApiTokenErrorResponse(int code, List<TokenErrorDetail> errors, String message) {
        this.code = code;
        this.status = "error";
        this.errors = errors;
        this.message = message;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TokenErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<TokenErrorDetail> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
