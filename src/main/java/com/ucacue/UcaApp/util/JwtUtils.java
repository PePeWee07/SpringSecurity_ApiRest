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
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtils {

    private static final String privateKey = "26aecab6ab011d486b2418c1bd2191c74a71405f1669d2ec4d0d4dc3775eda7c";

    private static final String userGenerator = "AUTH0JWT-BACKEND";

    public String createToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwtToken = JWT.create()
                .withIssuer(userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        
        return jwtToken;
    }

    public DecodedJWT validateToken(String token) {
        try {
            if (token == null) {
                throw new JWTVerificationException("Token is null");
            }

            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(userGenerator).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            return decodedJWT;

        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(e.getMessage());
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