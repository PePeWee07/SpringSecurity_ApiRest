package com.ucacue.UcaApp.exception.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucacue.UcaApp.model.dto.Api.ApiError;
import com.ucacue.UcaApp.model.dto.Api.ApiErrorResponse;
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

        List<ApiError> errors = new ArrayList<>();
        ApiErrorResponse errorResponse;
        if (exception instanceof TokenExpiredException) {
            logger.info("Token expired: {}", exception.getMessage());
            errors.add(new ApiError("Token expired", exception.getMessage()));
            errorResponse = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token has expired",
                    errors
            );
        } else if (exception instanceof JWTDecodeException) {
            logger.error("Token decode error: {}", exception.getMessage());
            errors.add(new ApiError("Token decode error", exception.getMessage()));
            errorResponse = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid token format",
                    errors
            );
        } else if (exception instanceof JWTVerificationException) {
            logger.error("Token verification error: {}", exception.getMessage());
            errors.add(new ApiError("Token verification error", exception.getMessage()));
            errorResponse = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token verification failed",
                    errors
            );
        } else if (exception instanceof MissingTokenException) {
            logger.error("Missing token: {}", exception.getMessage());
            errors.add(new ApiError("Missing token", exception.getMessage()));
            errorResponse = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token is missing",
                    errors
            );
        } else if(exception instanceof InvalidJwtTokenException) {
            logger.error("Invalid token: {}", exception.getMessage());
            errors.add(new ApiError("Invalid token", exception.getMessage()));
            errorResponse = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid token",
                    errors
            );
        } else {
            logger.error("Authentication error: {}", authException.getMessage());
            errors.add(new ApiError("Authentication error", authException.getMessage()));
            errorResponse = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication failed",
                    errors
            );
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}