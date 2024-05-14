package com.ucacue.UcaApp.web.response.fieldValidation;

public class FieldErrorDetail {
    private String field;
    private String error;
    private String rejectedValue;
    private String errorCode;

    // Constructor
    public FieldErrorDetail(String field, String error, String rejectedValue, String errorCode) {
        this.field = field;
        this.error = error;
        this.rejectedValue = rejectedValue;
        this.errorCode = errorCode;
    }

    // Getters and Setters
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}

