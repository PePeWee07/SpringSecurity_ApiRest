package com.ucacue.UcaApp.model.dto.auth;

import com.ucacue.UcaApp.model.entity.UserEntity;

public record AuthTokensResult(
        UserEntity user,
        String accessToken,
        String refreshToken) {
}