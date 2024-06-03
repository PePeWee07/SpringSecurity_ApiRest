package com.ucacue.UcaApp.web.response.AuthResponse;

import java.util.*;

public class AuthErrorResponse {
    private int code;
    private String status;
    private List<AuthErrorDetail> errors;
    private String message;

    // Constructor for error
    public AuthErrorResponse(int code, List<AuthErrorDetail> errors, String message) {
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
    
    public List<AuthErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<AuthErrorDetail> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

