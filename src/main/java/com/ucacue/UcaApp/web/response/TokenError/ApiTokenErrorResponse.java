package com.ucacue.UcaApp.web.response.TokenError;

public class ApiTokenErrorResponse {

    private int code;
    private String status;
    private TokenErrorDetail tokenErrorDetail;
    private String message;

    // Constructor for Error
    public ApiTokenErrorResponse(int code, String status, TokenErrorDetail tokenErrorDetail, String message) {
        this.code = code;
        this.status = status;
        this.tokenErrorDetail = tokenErrorDetail;
        this.message = message;
    }

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

    public TokenErrorDetail getTokenErrorDetail() {
        return tokenErrorDetail;
    }

    public void setTokenErrorDetail(TokenErrorDetail tokenErrorDetail) {
        this.tokenErrorDetail = tokenErrorDetail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

    

