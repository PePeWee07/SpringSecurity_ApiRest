package com.ucacue.UcaApp.controller.V2.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.auth.RefreshTokenRequest;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.service.admin.AdminMangerService;
import com.ucacue.UcaApp.service.token.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthenticationController", description = "Controlador para gestionar Auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AdminMangerService adminMangerService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/sign-up")
    @Operation(summary = "Registrarce", description = "Registro de nuevo Usuario.")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserRequestDto userRequest) {
        try {
            return new ResponseEntity<>(adminMangerService.RegisterUser(userRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.info("Error: {@POST /Sign-up}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/log-in")
    @Operation(summary = "Login", description = "Login de Usuario.")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid AuthLoginRequest authLoginRequest) {
        try {
            AuthResponse response = adminMangerService.loginUser(authLoginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("Error: {@POST /log-in}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/token-refresh")
    @Operation(summary = "Refrecar Token", description = "Resfresca el Token.")
    public ResponseEntity<AuthResponse> refreshUserToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse response = tokenService.refreshUserToken(refreshTokenRequest.refreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("Error: {@POST /token-refresh}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/log-out")
    @Operation(summary = "Cierre de sesion", description = "Anula el Token.")
    public ResponseEntity<ApiResponse> logoutUser(HttpServletRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            tokenService.revokeToken(username);

            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), null, "Successfully logged out"));
        } catch (Exception e) {
            logger.info("Error: {@POST /log-out}", e.getMessage());
            throw e;
        }
    }
}