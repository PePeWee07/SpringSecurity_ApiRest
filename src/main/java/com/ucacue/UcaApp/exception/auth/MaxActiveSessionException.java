package com.ucacue.UcaApp.exception.auth;

public class MaxActiveSessionException extends RuntimeException {

    public MaxActiveSessionException(String message) {
        super(message);
    }
}
