package com.ucacue.UcaApp.exception.token;

public class JwtTokenVerificationException extends JwtTokenException {
    public JwtTokenVerificationException(String message) {
        super(message);
    }
}