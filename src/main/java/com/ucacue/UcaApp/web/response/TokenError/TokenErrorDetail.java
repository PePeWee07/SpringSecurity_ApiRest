package com.ucacue.UcaApp.web.response.TokenError;

public class TokenErrorDetail {
    
    private String error;

    public TokenErrorDetail(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
