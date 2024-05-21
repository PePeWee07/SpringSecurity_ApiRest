package com.ucacue.UcaApp.exception.token;

public class JwtTokenException extends RuntimeException {
    public JwtTokenException(String message) {
        super(message);
    }
}

