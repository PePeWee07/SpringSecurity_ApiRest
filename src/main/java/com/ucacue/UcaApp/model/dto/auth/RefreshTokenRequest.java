package com.ucacue.UcaApp.model.dto.auth;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(@NotNull String refreshToken) {
}
