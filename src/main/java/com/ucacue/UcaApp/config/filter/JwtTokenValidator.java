package com.ucacue.UcaApp.config.filter;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ucacue.UcaApp.exception.token.MissingTokenException;
import com.ucacue.UcaApp.service.token.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.util.token.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    private JwtUtils jwtUtils;
    
    @Autowired
    TokenService tokenService;

    public JwtTokenValidator(JwtUtils jwtUtils, TokenService tokenService) {
        this.jwtUtils = jwtUtils;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken == null || jwtToken.isEmpty() || !jwtToken.startsWith("Bearer ")) {
            request.setAttribute("exception", new MissingTokenException("JWT token is missing or empty"));
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = jwtToken.replace("Bearer ", "");

        if (tokenService.isTokenRevoked(jwtToken)) {
            SecurityContextHolder.clearContext();
            logger.error("Token has been revoked");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
            return;
        }

        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            String username = jwtUtils.getUsernameFromToken(decodedJWT);
            String authorities = jwtUtils.getClaimFromToken(decodedJWT, "authorities").asString();
            Collection<? extends GrantedAuthority> authoritiesList = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(authorities);
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesList);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (JWTVerificationException e) {
            SecurityContextHolder.clearContext();
            logger.error("JWT verification failed: {}", e.getMessage());
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }
}
