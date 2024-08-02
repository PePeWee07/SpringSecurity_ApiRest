package com.ucacue.UcaApp.service.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ucacue.UcaApp.model.entity.RevokedTokenEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenService {

    void revokeToken(String token, String email);
    boolean isTokenRevoked(String token);
    String extractTokenFromRequest(HttpServletRequest request);
    
    List<RevokedTokenEntity> findAllRevokedToken();
    RevokedTokenEntity findRevokedTokenById(Long id);
    Optional<RevokedTokenEntity> findRevokedTokenByEmail(String email);
    List<RevokedTokenEntity> getTokensRevokedBetween(LocalDateTime startDate, LocalDateTime endDate);
}
