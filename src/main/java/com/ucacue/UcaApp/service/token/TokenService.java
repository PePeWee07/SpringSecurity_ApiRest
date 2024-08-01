package com.ucacue.UcaApp.service.token;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenService {

    void revokeToken(String token, String email);
    boolean isTokenRevoked(String token);
    String extractTokenFromRequest(HttpServletRequest request);
}
