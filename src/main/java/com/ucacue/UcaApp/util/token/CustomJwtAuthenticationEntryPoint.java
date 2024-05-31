package com.ucacue.UcaApp.util.token;

import org.hibernate.mapping.Collection;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucacue.UcaApp.web.response.TokenError.ApiTokenErrorResponse;
import com.ucacue.UcaApp.web.response.TokenError.TokenErrorDetail;
import com.ucacue.UcaApp.web.response.fieldValidation.FieldErrorDetail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class CustomJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Throwable exception = (Throwable) request.getAttribute("exception");

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiTokenErrorResponse errorResponse;
        if (exception instanceof com.auth0.jwt.exceptions.TokenExpiredException) {
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "error",
                    LIST,
                    "Token has expired"
            );
        } else if (exception instanceof com.auth0.jwt.exceptions.JWTDecodeException) {
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "error",
                    LIST,
                    "Token decoding error"
            );
        } else if (exception instanceof com.auth0.jwt.exceptions.JWTVerificationException) {
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "error",
                    LIST,
                    "Token verification error"
            );
        } else {
            errorResponse = new ApiTokenErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "error",
                    LIST,
                    authException.getMessage()
            );
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}