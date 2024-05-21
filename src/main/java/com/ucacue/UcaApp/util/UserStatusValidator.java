package com.ucacue.UcaApp.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

public class UserStatusValidator {

    public static void validate(UserDetails userDetails) {
        if (!userDetails.isEnabled()) {
            throw new DisabledException("Account disabled");
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("Account expired");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("Account locked");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Credentials expired");
        }
    }
}
