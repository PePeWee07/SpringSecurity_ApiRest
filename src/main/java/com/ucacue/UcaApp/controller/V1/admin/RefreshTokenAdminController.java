package com.ucacue.UcaApp.controller.V1.admin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.token.RefreshTokenResponseDto;
import com.ucacue.UcaApp.service.token.RefreshTokenAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/admin/refresh-tokens")
@Tag(name = "Controlador Admin Refresh Tokens",
     description = "Gestion administrativa de refresh tokens activos: listado paginado con filtros y eliminacion individual o por usuario.")
public class RefreshTokenAdminController {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenAdminController.class);

    @Autowired
    private RefreshTokenAdminService refreshTokenAdminService;

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refresh tokens paginados",
               description = "Listado paginado con filtros opcionales: userId, email, jti, revoked, expired.")
    public ResponseEntity<Page<RefreshTokenResponseDto>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "expiresAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String jti,
            @RequestParam(required = false) Boolean revoked,
            @RequestParam(required = false) Boolean expired) {
        try {
            Sort sort = Sort.by(sortBy);
            sort = "asc".equalsIgnoreCase(direction) ? sort.ascending() : sort.descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            return ResponseEntity.ok(
                refreshTokenAdminService.findPaged(userId, email, jti, revoked, expired, pageable)
            );
        } catch (Exception e) {
            logger.info("Error: {@GET /admin/refresh-tokens/page} {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un refresh token",
               description = "Elimina un refresh token puntual por su id de registro.")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            refreshTokenAdminService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.info("Error: {@DELETE /admin/refresh-tokens/{}} {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar todos los refresh tokens de un usuario",
               description = "Borra TODOS los refresh tokens asociados al usuario indicado. Util para cerrar sesiones forzadamente.")
    public ResponseEntity<Map<String, Object>> deleteAllByUserId(@PathVariable Long userId) {
        try {
            long deleted = refreshTokenAdminService.deleteAllByUserId(userId);
            Map<String, Object> body = new HashMap<>();
            body.put("userId", userId);
            body.put("deleted", deleted);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.info("Error: {@DELETE /admin/refresh-tokens/user/{}} {}", userId, e.getMessage());
            throw e;
        }
    }
}
