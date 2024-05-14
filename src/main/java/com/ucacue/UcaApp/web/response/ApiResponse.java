package com.ucacue.UcaApp.web.response;


public class ApiResponse {

    private int code;
    private String status;
    //private String etag;
    private Object data;
    private String message;

    // Constructor for OK
    public ApiResponse(int code, Object data, String message) {
        this.code = code;
        this.status = "OK";
        this.data = (data != null) ? data : null; 
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
