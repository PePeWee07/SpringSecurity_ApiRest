package com.ucacue.UcaApp.model.dto.auth;

import jakarta.validation.constraints.NotNull;

public record AuthLoginRequest(@NotNull String username,@NotNull String password) {
}
