package com.ucacue.UcaApp.service.token.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.model.entity.RevokedTokenEntity;
import com.ucacue.UcaApp.repository.RevokedTokenRepository;
import com.ucacue.UcaApp.service.token.TokenService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    public void revokeToken(String token, String email) {
        RevokedTokenEntity revokedToken = new RevokedTokenEntity(token, email);
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
