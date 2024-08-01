package com.ucacue.UcaApp.service.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.model.entity.RevokedToken;
import com.ucacue.UcaApp.repository.RevokedTokenRepository;

@Service
public class TokenService {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    public void revokeToken(String token, String email) {
        RevokedToken revokedToken = new RevokedToken(token, email);
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
}
