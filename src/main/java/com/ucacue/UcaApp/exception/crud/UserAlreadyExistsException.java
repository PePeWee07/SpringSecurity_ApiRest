package com.ucacue.UcaApp.exception.crud;

public class UserAlreadyExistsException extends RuntimeException {
    private String email;

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
