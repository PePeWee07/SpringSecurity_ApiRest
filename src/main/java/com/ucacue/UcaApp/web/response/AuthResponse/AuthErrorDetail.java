package com.ucacue.UcaApp.web.response.AuthResponse;

public class AuthErrorDetail {
    private long status;
    private String message;
    private String error;

    public AuthErrorDetail() {
    }

    public AuthErrorDetail(long status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
