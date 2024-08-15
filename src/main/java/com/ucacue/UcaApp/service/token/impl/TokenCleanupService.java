package com.ucacue.UcaApp.service.token.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.service.token.TokenService;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TokenCleanupService {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TokenCleanupService.class);

    @Autowired
    private TokenService tokenService;

    // Este método se ejecutará cada 1 minuto
    @Scheduled(cron = "0 */1 * * * ?") 
    public void cleanUpExpiredTokens() {
        logger.info("Cron job started: Cleaning up expired tokens");
        tokenService.checkAndCleanExpiredRevokedTokens();
    }

    // Método público para ejecutar manualmente la limpieza
    public void runCleanupManually() {
        logger.info("Manual cleanup started: Cleaning up expired tokens");
        cleanUpExpiredTokens();
    }
}
