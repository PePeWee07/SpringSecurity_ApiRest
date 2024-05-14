package com.ucacue.UcaApp.web.response.fieldValidation;

import java.util.*;

public class FieldValidationResponse {
    private int code;
    private String status;
    private List<FieldErrorDetail> errors;
    private String message;

    // Constructor for error
    public FieldValidationResponse(int code, List<FieldErrorDetail> errors, String message) {
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
    
    public List<FieldErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldErrorDetail> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

