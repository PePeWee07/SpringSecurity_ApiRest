package com.ucacue.UcaApp.web.response.userNotFound;

import java.util.List;
import java.util.Map;

public class UserNotFoundResponse {
    private int code;
    private String status;
    private List<Map.Entry<String, String>> errors;
    private String message;

    // Constructor for error
    public UserNotFoundResponse(int code, List<Map.Entry<String, String>> errors, String message) {
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
    
    public List<Map.Entry<String, String>> getErrors() {
        return errors;
    }

    public void setErrors(List<Map.Entry<String, String>> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

