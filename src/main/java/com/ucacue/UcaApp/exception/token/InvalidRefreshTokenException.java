package com.ucacue.UcaApp.exception.token;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
