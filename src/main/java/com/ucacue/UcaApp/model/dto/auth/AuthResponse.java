package com.ucacue.UcaApp.model.dto.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "username", "message", "roles", "accessToken", "status" })
public record AuthResponse(
        String username,
        String message,
        List<String> roles,
        String accessToken,
        Boolean status) {
}