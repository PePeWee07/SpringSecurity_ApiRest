package com.ucacue.UcaApp.util.token;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.config.SecurityProperties;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class JwtUtils {

    private final SecurityProperties securityProperties;

    public JwtUtils (SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String createToken(Authentication authentication) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(securityProperties.getPrivateKey());

            String username = authentication.getName();
            String authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            return JWT.create()
                .withClaim("type", "access")
                .withIssuer(securityProperties.getUserGenerator())
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + securityProperties.getAccessTokenDuration().toMillis()))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        } catch (Exception e) {
            throw e;
        }
    }

    public String createRefreshToken(Authentication authentication) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(securityProperties.getPrivateKey());

            String username = authentication.getName();

            return JWT.create()
                    .withClaim("type", "refresh")
                    .withIssuer(securityProperties.getUserGenerator())
                    .withSubject(username)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + securityProperties.getRefreshTokenDuration().toMillis()))
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm);
        } catch (Exception e) {
            throw e;
        }
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(securityProperties.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(securityProperties.getUserGenerator()).build();
            return verifier.verify(token);
        } catch (Exception e) {
            throw e;
        }
    }

    public LocalDateTime getExpiryDateFromToken(DecodedJWT token) {
        Claim expClaim = token.getClaim("exp");
        if (expClaim.isNull()) {
            throw new RuntimeException("Token does not have an expiration claim");
        }
        Date expiryDate = expClaim.asDate();
        return LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault());
    }

    public boolean isRefreshToken(DecodedJWT decodedJWT) {
        return Optional.ofNullable(decodedJWT.getClaim("type").asString())
                .map(type -> type.equals("refresh"))
                .orElse(false);
    }

    public String getUsernameFromToken(DecodedJWT token) {
        return token.getSubject().toString();
    }

    public Claim getClaimFromToken(DecodedJWT token, String claim) {
        return token.getClaim(claim);
    }

    public Map<String, Claim> getClaimsFromToken(DecodedJWT token) {
        return token.getClaims();
    }

    public LocalDateTime getExpirationDate(String refreshToken) {
        try {
            DecodedJWT decodedJWT = validateToken(refreshToken);
            return LocalDateTime.ofInstant(decodedJWT.getExpiresAt().toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            throw e;
        }
    }

    public String hashToken(String token) {
        return passwordEncoder.encode(token);
    }

}