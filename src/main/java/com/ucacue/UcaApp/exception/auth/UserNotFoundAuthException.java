package com.ucacue.UcaApp.exception.auth;

public class UserNotFoundAuthException extends RuntimeException{
    
    public UserNotFoundAuthException(String message){
        super(message);
    }
}
