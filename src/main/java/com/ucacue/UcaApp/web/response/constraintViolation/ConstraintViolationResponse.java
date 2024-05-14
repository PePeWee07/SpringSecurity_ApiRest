package com.ucacue.UcaApp.web.response.constraintViolation;

import java.util.List;

public class ConstraintViolationResponse {
    private int code;
    private String status;
    private List<ConstraintErrorDetail> errors;
    private String message;

    // Constructor for error
    public ConstraintViolationResponse(int code, List<ConstraintErrorDetail> errors, String message) {
        this.code = code;
        this.status = "error";
        this.errors = errors;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<ConstraintErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ConstraintErrorDetail> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

