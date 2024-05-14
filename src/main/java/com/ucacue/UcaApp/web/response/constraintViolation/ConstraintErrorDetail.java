package com.ucacue.UcaApp.web.response.constraintViolation;

public class ConstraintErrorDetail {
    private String field;
    private String error;
    private String errorCode;

    public ConstraintErrorDetail(String field, String error, String errorCode) {
        this.field = field;
        this.error = error;
        this.errorCode = errorCode;
    }

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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
