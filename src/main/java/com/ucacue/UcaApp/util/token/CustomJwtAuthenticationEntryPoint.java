package com.ucacue.UcaApp.util.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucacue.UcaApp.exception.token.InvalidJwtTokenException;
import com.ucacue.UcaApp.exception.token.MissingTokenException;
import com.ucacue.UcaApp.web.response.TokenError.ApiTokenErrorResponse;
import com.ucacue.UcaApp.web.response.TokenError.TokenErrorDetail;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomJwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Throwable exception = (Throwable) request.getAttribute("exception");

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        List<TokenErrorDetail> errors = new ArrayList<>();
        ApiTokenErrorResponse errorResponse;
        if (exception instanceof TokenExpiredException) {
            logger.error("Token expired: {}", exception.getMessage());
            errors.add(new TokenErrorDetail(exception.getMessage()));
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    errors,
                    "Token has expired"
            );
        } else if (exception instanceof JWTDecodeException) {
            logger.error("Token decoding error: {}", exception.getMessage());
            errors.add(new TokenErrorDetail(exception.getMessage()));
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    errors,
                    "Token decoding error"
            );
        } else if (exception instanceof JWTVerificationException) {
            logger.error("Token verification error: {}", exception.getMessage());
            errors.add(new TokenErrorDetail("Unsupported JWT token"));
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    errors,
                    "Token verification error"
            );
        } else if (exception instanceof MissingTokenException) {
            logger.error("Missing or empty token: {}", exception.getMessage());
            errors.add(new TokenErrorDetail(exception.getMessage()));
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    errors,
                    exception.getMessage()
            );
        } else if (exception instanceof InvalidJwtTokenException) {
            logger.error("Invalid token due to user state: {}", exception.getMessage());
            errors.add(new TokenErrorDetail(exception.getMessage()));
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    errors,
                    exception.getMessage()
            );
        }else {
            logger.error("Token Error: {}", exception.getMessage());
            errors.add(new TokenErrorDetail(exception.getMessage()));
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    errors,
                    authException.getMessage()
            );
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}