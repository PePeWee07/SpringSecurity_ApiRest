package com.ucacue.UcaApp.util.token;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
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

import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtils {

    @Value("${security.private-key}")
    private String privateKey;

    @Value("${security.user-generator}")
    private String userGenerator;

    public String createToken(Authentication authentication) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);

            String username = authentication.getName();
            String authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            return JWT.create()
                    .withIssuer(userGenerator)
                    .withSubject(username)
                    .withClaim("authorities", authorities)
                    .withIssuedAt(new Date())
                    // .withExpiresAt(new Date(System.currentTimeMillis() + 1800000)) // 30 minutes
                    .withExpiresAt(new Date(System.currentTimeMillis() + 28800000)) // 8 hours
                    // .withExpiresAt(new Date(System.currentTimeMillis() + 60000)) // 1 min
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm);
        } catch (Exception e) {
            throw e;
        }
    }

    public String createRefreshToken(Authentication authentication) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);

            String username = authentication.getName();

            return JWT.create()
                    .withIssuer(userGenerator)
                    .withSubject(username)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 864000000)) // 10 days
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm);
        } catch (Exception e) {
            throw e;
        }
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(userGenerator).build();
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

    public String getUsernameFromToken(DecodedJWT token) {
            return token.getSubject().toString();
    }

    public Claim getClaimFromToken(DecodedJWT token, String claim) {
            return token.getClaim(claim);
    }

    public Map<String, Claim> getClaimsFromToken(DecodedJWT token) {
            return token.getClaims();
    }

}