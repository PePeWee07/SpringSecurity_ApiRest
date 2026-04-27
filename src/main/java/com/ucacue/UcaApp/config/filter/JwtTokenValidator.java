package com.ucacue.UcaApp.config.filter;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.exception.token.InvalidJwtTokenException;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.token.TokenService;
import com.ucacue.UcaApp.util.token.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public JwtTokenValidator(JwtUtils jwtUtils, TokenService tokenService, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || authorizationHeader.isBlank()
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorizationHeader.substring(7);

        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            String username = jwtUtils.getUsernameFromToken(decodedJWT);

            String tokenType = jwtUtils.getClaimFromToken(decodedJWT, "type").asString();
            if (!"access".equals(tokenType)) {
                SecurityContextHolder.clearContext();
                throw new InvalidJwtTokenException("Invalid token type for resource access");
            }

            UserEntity user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found in Database"));

            if (!user.isEnabled() || !user.isAccountNonLocked() || !user.isAccountNonExpired()
                    || !user.isCredentialsNonExpired()) {
                tokenService.revokeToken(user.getEmail());
                SecurityContextHolder.clearContext();
                throw new InvalidJwtTokenException("Token has been revoked due to user state");
            }

            String authorities = jwtUtils.getClaimFromToken(decodedJWT, "authorities").asString();
            Collection<? extends GrantedAuthority> authoritiesList = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(authorities);

            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            boolean needsAuthenticationUpdate = currentAuth == null
                    || !currentAuth.isAuthenticated()
                    || !username.equals(currentAuth.getName());

            if (needsAuthenticationUpdate) {
                UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                        .authenticated(username, null, authoritiesList);
                authentication.setDetails(user);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }

        } catch (JWTVerificationException | InvalidJwtTokenException e) {
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", e);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            logger.error("Error on JwtTokenValidator.java", e);
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/csrf")
                || path.startsWith("/auth/log-in")
                || path.startsWith("/auth/token-refresh")
                || path.startsWith("/auth/sign-up")
                || path.startsWith("/auth/log-out")
                || path.startsWith("/api/v1/catia/core/health");
    }
}
