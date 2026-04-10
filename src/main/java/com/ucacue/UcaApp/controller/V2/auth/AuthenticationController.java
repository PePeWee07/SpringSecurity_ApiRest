package com.ucacue.UcaApp.controller.V2.auth;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.config.SecurityProperties;
import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthTokensResult;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.service.admin.AdminMangerService;
import com.ucacue.UcaApp.service.token.TokenService;
import com.ucacue.UcaApp.service.token.impl.AuthCookieService;
import com.ucacue.UcaApp.util.cookie.CookieUtils;
import com.ucacue.UcaApp.util.token.JwtUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthenticationController", description = "Controlador para gestionar Auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AdminMangerService adminMangerService;
    private final TokenService tokenService;
    private final AuthCookieService authCookieService;
    private final JwtUtils jwtUtils;
    private final SecurityProperties securityProperties;

    public AuthenticationController(
            AdminMangerService adminMangerService,
            TokenService tokenService,
            AuthCookieService authCookieService,
            JwtUtils jwtUtils,
            SecurityProperties securityProperties) {
        this.adminMangerService = adminMangerService;
        this.tokenService = tokenService;
        this.authCookieService = authCookieService;
        this.jwtUtils = jwtUtils;
        this.securityProperties = securityProperties; 
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Registrarce", description = "Registro de nuevo Usuario.")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserRequestDto userRequest) {
        try {
            return new ResponseEntity<>(adminMangerService.RegisterUser(userRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error: {@POST /sign-up}", e);
            throw e;
        }
    }

    @PostMapping("/log-in")
    @Operation(summary = "Login", description = "Login de Usuario.")
    public ResponseEntity<AuthResponse> loginUser(
            @RequestBody @Valid AuthLoginRequest authLoginRequest,
            HttpServletResponse response) {
        try {
            AuthTokensResult result = adminMangerService.loginUser(authLoginRequest);

            authCookieService.addRefreshTokenCookie(response, result.refreshToken());

            AuthResponse authResponse = buildAuthResponse(
                    result.user(),
                    "User logged in successfully",
                    result.accessToken());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Error: {@POST /log-in}", e);
            throw e;
        }
    }

    @PostMapping("/token-refresh")
    @Operation(summary = "Refrescar Token", description = "Refresca el Token.")
    public ResponseEntity<AuthResponse> refreshUserToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String refreshToken = CookieUtils.getCookieValue(request, securityProperties.getRefreshTokenCookie())
                    .orElseThrow(() -> new RuntimeException("Refresh token cookie not found"));

            AuthTokensResult result = tokenService.refreshUserToken(refreshToken);

            authCookieService.addRefreshTokenCookie(response, result.refreshToken());

            AuthResponse authResponse = buildAuthResponse(
                    result.user(),
                    "Token refreshed successfully",
                    result.accessToken());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Error: {@POST /token-refresh}", e);
            throw e;
        }
    }

    @PostMapping("/log-out")
    @Operation(summary = "Cierre de sesión", description = "Anula el Token.")
    public ResponseEntity<ApiResponse> logoutUser(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String refreshToken = CookieUtils.getCookieValue(request, securityProperties.getRefreshTokenCookie())
                    .orElseThrow(() -> new RuntimeException("Refresh token cookie not found"));

            DecodedJWT decodedJWT = jwtUtils.validateToken(refreshToken);

            if (!jwtUtils.isRefreshToken(decodedJWT)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String email = jwtUtils.getUsernameFromToken(decodedJWT);

            tokenService.revokeToken(email);
            authCookieService.clearRefreshTokenCookie(response);

            return ResponseEntity.ok(
                    new ApiResponse(HttpStatus.OK.value(), null, "Successfully logged out"));
        } catch (Exception e) {
            logger.error("Error: {@POST /log-out}", e);
            throw e;
        }
    }

    private AuthResponse buildAuthResponse(UserEntity user, String message, String accessToken) {
        List<String> roles = user.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return new AuthResponse(
                user.getEmail(),
                message,
                roles,
                accessToken,
                true);
    }
}