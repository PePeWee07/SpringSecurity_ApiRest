package com.ucacue.UcaApp.controller.V2.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.service.token.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2")
@Tag(name = "RevokedTokenController_v2", description = "Controlador para gestionar Tokens Revocados")
public class RevokedTokenController_v2 {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RevokedTokenController_v2.class);

    @Autowired
    private TokenService tokenService;

    //Ejecutar limpieza manual
    @DeleteMapping("/revoked/tokens/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restabalecer Limite de sessiones del usuario", description = "Eliminara todo los Token Refresh registrados del usuario, solo ejecutar si el usaurio no puede hacer un Logout de donde quedo abierta la sesion")
    public ApiResponse runCleanup(@PathVariable String email) {
        try {
            ApiResponse resp = tokenService.cleanTokenRefreshForUser(email);
            return resp;
        } catch (Exception e) {
            logger.info("Error: {@DELETE /revoked/token}", e.getMessage());
            throw e;
        }
    }
}
