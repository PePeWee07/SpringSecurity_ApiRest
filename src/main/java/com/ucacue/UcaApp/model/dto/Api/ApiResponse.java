package com.ucacue.UcaApp.model.dto.Api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private int status;
    private Object data;
    private String message;

    // Constructor for OK
    public ApiResponse(int status, Object data, String message) {
        this.status = status;
        this.data = data; 
        this.message = message;
    }

    public int getStatus(){
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

}
