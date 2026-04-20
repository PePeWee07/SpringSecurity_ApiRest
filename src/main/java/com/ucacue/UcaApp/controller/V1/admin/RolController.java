package com.ucacue.UcaApp.controller.V1.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.service.rol.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Controlador de Roles", description = "Controlador para gestionar Roles")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista de Roles", description = "Listado de todo los Roles.")
    public ResponseEntity<List<RoleResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(rolService.findAll());
        } catch (Exception e) {
            logger.info("Error: {@GET /roles}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busqueda de Rol por ID", description = "Obtiene los datos del Rol.")
    public ResponseEntity<RoleResponseDto> findById(@PathVariable Long id) {
        try {
            RoleResponseDto response = rolService.getRoleById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("Error: {@GET /rol/{id}}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear Rol", description = "Crea un nuevo Rol.")
    public ResponseEntity<RoleResponseDto> create(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        try {
            RoleResponseDto savedRol = rolService.save(roleRequestDto);
            return ResponseEntity.ok(savedRol);
        } catch (Exception e) {
            logger.info("Error: {@POST /rol}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar Rol", description = "Actualiza los datos de un Rol.")
    public ResponseEntity<RoleResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody RoleRequestDto roleRequestDto) {
        try {
            RoleResponseDto updatedRol = rolService.update(id, roleRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedRol);
        } catch (Exception e) {
            logger.info("Error: {@PUT /rol/{id}}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar Rol", description = "Elimina un Rol.")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            rolService.deleteRoleById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.info("Error: {@DELETE /role/{id}}", e.getMessage());
            throw e;
        }
    }
}
