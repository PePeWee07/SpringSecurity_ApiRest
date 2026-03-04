package com.ucacue.UcaApp.service.token;
import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.entity.RefreshTokenEntity;

public interface TokenService {

    AuthResponse refreshUserToken(String refreshToken);
    void revokeToken(String email);
    RefreshTokenEntity getValidRefreshTokenByJti(String jti);
    void saveTokenRefresh(RefreshTokenEntity refreshToken);
    void limitSession(String email);
    ApiResponse cleanTokenRefreshForUser(String email);

}
