package com.ucacue.UcaApp.web.response.keyViolateUnique;

import java.util.*;

public class KeyViolateUniqueResponse {
    private int code;
    private String status;
    private List<KeyViolateDetail> errors;
    private String message;

    // Constructor for error
    public KeyViolateUniqueResponse(int code, List<KeyViolateDetail> errors, String message) {
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
    
    public List<KeyViolateDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<KeyViolateDetail> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

