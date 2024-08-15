package com.ucacue.UcaApp.controller.V2.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.entity.RevokedTokenEntity;
import com.ucacue.UcaApp.service.token.TokenService;
import com.ucacue.UcaApp.service.token.impl.TokenCleanupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2")
@Tag(name = "RevokedTokenController_v2", description = "Controlador para gestionar Tokens Revocados")
public class RevokedTokenController_v2 {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RevokedTokenController_v2.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenCleanupService tokenCleanupService;

    //Ejecutar limpieza manual
    @GetMapping("/revokedTokens/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Limpiar tokens revocados", description = "Limpia los tokens expirados manualmente.")
    public ResponseEntity<String> runCleanup() {
        try {
            tokenCleanupService.runCleanupManually();
            return ResponseEntity.ok("Manual cleanup executed successfully.");
        } catch (Exception e) {
            logger.info("Error: {@GET /revokedTokens/cleanup}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //Listar Tokens Revocados
    @GetMapping("/revokedTokens")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tokens revocados", description = "Listado de todo los Token revocados.")
    public ResponseEntity<List<RevokedTokenEntity>> findAllRevokedToken(){
        try {
            return ResponseEntity.ok(tokenService.findAllRevokedToken());
        } catch (Exception e) {
            logger.info("Error: {@GET /revokedTokens}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //Buscar Tokens Revocados por id
    @GetMapping("/revokedToken/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Token revocado", description = "buscar Token revocado por id.")
    public ResponseEntity<?> findRevokedTokenById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(tokenService.findRevokedTokenById(id));
        } catch (Exception e) {
            logger.info("Error: {@GET /revokedToken/{id}}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //Buscar Tokens Revocados por email
    @GetMapping("/revokedToken/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Token revocado", description = "buscar Token revocado por email.")
    public ResponseEntity<?> findRevokedTokenByEmail(@PathVariable String email){
        try {
            return ResponseEntity.ok(tokenService.findRevokedTokenByEmail(email));
        } catch (Exception e) {
            logger.info("Error: {@GET /revokedToken/email/{email}}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //Buscar por el tiempo de revokedAt
    @GetMapping("/revoked-tokens")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Token revocado", description = "buscar Token revocado por fecha de revocación formato(yyyy-MM-dd'T'HH:mm:ss).")
    public ResponseEntity<List<RevokedTokenEntity>> getRevokedTokens(
            @RequestParam String startDate,
            @RequestParam String endDate) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                    LocalDateTime end = LocalDateTime.parse(endDate, formatter);
                    List<RevokedTokenEntity> revokedTokens = tokenService.getTokensRevokedBetween(start, end);
                    return ResponseEntity.ok(revokedTokens);
                } catch (Exception e) {
                    logger.info("Error: {@GET /revokedToken/revokedAt/{startDate}/{endDate}}", e.getMessage());
                    return ResponseEntity.badRequest().build();
                }
    }
}
