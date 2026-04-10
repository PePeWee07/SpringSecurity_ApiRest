package com.ucacue.UcaApp.service.token.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.config.SecurityProperties;
import com.ucacue.UcaApp.exception.auth.MaxActiveSessionException;
import com.ucacue.UcaApp.exception.crud.UserNotFoundException;
import com.ucacue.UcaApp.exception.token.InvalidRefreshTokenException;
import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthTokensResult;
import com.ucacue.UcaApp.model.entity.RefreshTokenEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.repository.RefreshTokenRepository;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.token.TokenService;
import com.ucacue.UcaApp.util.token.JwtUtils;

@Service
public class TokenServiceImpl implements TokenService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TokenService.class);


    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final SecurityProperties securityProperties;

    public TokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtUtils jwtUtils,
            SecurityProperties securityProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.securityProperties = securityProperties;
    }


    @Transactional
    @Override
    public AuthTokensResult refreshUserToken(String refreshToken) {
        DecodedJWT decodedJWT = jwtUtils.validateToken(refreshToken);

        if (!jwtUtils.isRefreshToken(decodedJWT)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String username = jwtUtils.getUsernameFromToken(decodedJWT);
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jti = decodedJWT.getId();

        RefreshTokenEntity storedToken = getValidRefreshTokenByJti(jti);

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        storedToken.setRevoked(true);
        saveTokenRefresh(storedToken);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getAuthorities());

        String newAccessToken = jwtUtils.createToken(authentication);
        String newRefreshToken = jwtUtils.createRefreshToken(authentication);

        DecodedJWT newDecoded = jwtUtils.validateToken(newRefreshToken);

        RefreshTokenEntity newToken = new RefreshTokenEntity();
        newToken.setJti(newDecoded.getId());
        newToken.setUser(user);
        newToken.setRevoked(false);
        newToken.setExpiresAt(jwtUtils.getExpirationDate(newRefreshToken));

        saveTokenRefresh(newToken);

        return new AuthTokensResult(
                user,
                newAccessToken,
                newRefreshToken);
    }

    // ---- TOKEN REFRESH ----

    @Transactional
    @Override
    public void revokeToken(String email) {
        UserEntity user = userRepository.findByEmailWithLock(email)
                .orElseThrow(() -> new UserNotFoundException(email, UserNotFoundException.SearchType.EMAIL));
        refreshTokenRepository.deleteAllByUser(user);
    }

    @Override
    public RefreshTokenEntity getValidRefreshTokenByJti(String jti) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByJtiAndRevokedFalse(jti)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid"));
        return refreshToken;
    }

    @Override
    public void saveTokenRefresh(RefreshTokenEntity refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void limitSession(String email) {

        int allowedSessions = securityProperties.getMaxSessions();

        long activeSessions = refreshTokenRepository
                .countByUserEmailAndRevokedFalseAndExpiresAtAfter(
                        email,
                        LocalDateTime.now());

        if (activeSessions >= allowedSessions) {
            throw new MaxActiveSessionException(
                    "Multiple active sessions, limited to " + allowedSessions);
        }
    }

    // ---- CRON TOKEN REFRESH CLEAN ----
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void cleanUpExpiredRefreshTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        logger.info("Expired refresh tokens cleaned.");
    }

    @Override
    @Transactional
    public ApiResponse cleanTokenRefreshForUser(String email) {

        revokeToken(email);

        return new ApiResponse(
                HttpStatus.OK.value(),
                null,
                "All refresh tokens successfully deleted for user: " + email
        );
    }
}
