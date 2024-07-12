package com.ucacue.UcaApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "username", "message", "status", "jwt" })
public record AuthResponse(
                String username,
                String message,
                String jwt,
                String refreshToken,
                Boolean status) {
}
