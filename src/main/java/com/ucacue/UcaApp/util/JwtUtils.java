package com.ucacue.UcaApp.util;

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
                    .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
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