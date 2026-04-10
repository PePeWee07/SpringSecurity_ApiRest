package com.ucacue.UcaApp.service.token.impl;

import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.config.SecurityProperties;

@Service
public class AuthCookieService {

    private final SecurityProperties securityProperties;

    public AuthCookieService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        addCookie(
                response,
                securityProperties.getRefreshTokenCookie(),
                token,
                securityProperties.getRefreshTokenPath(),
                securityProperties.getRefreshTokenDuration(),
                securityProperties.getRefreshTokenSameSite());
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        clearCookie(
                response,
                securityProperties.getRefreshTokenCookie(),
                securityProperties.getRefreshTokenPath(),
                securityProperties.getRefreshTokenSameSite());
    }

    private void addCookie(HttpServletResponse response, String name, String value,
            String path, Duration maxAge, String sameSite) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(securityProperties.isCookieSecure())
                .path(path)
                .sameSite(sameSite)
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name, String path, String sameSite) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(securityProperties.isCookieSecure())
                .path(path)
                .sameSite(sameSite)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}