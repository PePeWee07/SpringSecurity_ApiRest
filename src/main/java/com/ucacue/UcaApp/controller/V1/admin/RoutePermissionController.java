package com.ucacue.UcaApp.controller.V1.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.routepermission.RoutePermissionAssignDto;
import com.ucacue.UcaApp.model.dto.routepermission.RoutePermissionResponseDto;
import com.ucacue.UcaApp.model.dto.routepermission.RoutePermissionSyncDto;
import com.ucacue.UcaApp.service.routepermission.RoutePermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Controlador de Permisos de Rutas",
     description = "Gestiona el mapping ruta->roles del back-office (dinamico desde la UI)")
public class RoutePermissionController {

    private static final Logger logger = LoggerFactory.getLogger(RoutePermissionController.class);

    @Autowired
    private RoutePermissionService routePermissionService;

    @GetMapping("/route-permissions")
    @Operation(summary = "Lista de permisos de rutas",
               description = "Listado completo de rutas con sus roles asignados. Usado por el AuthGuard del front.")
    public ResponseEntity<List<RoutePermissionResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(routePermissionService.findAll());
        } catch (Exception e) {
            logger.info("Error: {@GET /route-permissions} {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/route-permission/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar roles a una ruta",
               description = "Reemplaza el conjunto de roles que pueden acceder a la ruta indicada.")
    public ResponseEntity<RoutePermissionResponseDto> assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody RoutePermissionAssignDto body) {
        try {
            return ResponseEntity.ok(routePermissionService.assignRoles(id, body.getRoleIds()));
        } catch (Exception e) {
            logger.info("Error: {@PUT /route-permission/{}/roles} {}", id, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/route-permissions/sync")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sincronizar rutas del front",
               description = "Registra en BD las rutas nuevas enviadas por el front; las existentes no se tocan.")
    public ResponseEntity<List<RoutePermissionResponseDto>> sync(@Valid @RequestBody RoutePermissionSyncDto body) {
        try {
            return ResponseEntity.ok(routePermissionService.syncPaths(body.getPaths()));
        } catch (Exception e) {
            logger.info("Error: {@POST /route-permissions/sync} {}", e.getMessage());
            throw e;
        }
    }
}
