package com.ucacue.UcaApp.util.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        try {
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            return null;
        }
    }

    public void verifyPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
