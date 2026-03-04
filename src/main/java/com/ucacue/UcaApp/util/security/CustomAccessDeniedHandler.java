package com.ucacue.UcaApp.util.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucacue.UcaApp.model.dto.Api.ApiError;
import com.ucacue.UcaApp.model.dto.Api.ApiErrorResponse;

import java.io.IOException;
import java.util.List;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex)
            throws IOException {

        ApiError apiError = new ApiError(
                "You do not have permission to access this resource",
                null);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                List.of(apiError));

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(errorResponse));
    }
}