package com.ucacue.UcaApp.controller.V2.admin;

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
import com.ucacue.UcaApp.web.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2")
public class RolController_v2 {

    private static final Logger logger = LoggerFactory.getLogger(RolController_v2.class);

    @Autowired
    private RolService rolService;

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(rolService.findAll());
        } catch (Exception e) {
            logger.info("Error: {@GET /roles}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/rol/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> findById(@PathVariable Long id) {
        try {
            RoleResponseDto response = rolService.getRoleById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("Error: {@GET /rol/{id}}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        try {
            RoleResponseDto savedRol = rolService.save(roleRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedRol, "Rol created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.info("Error: {@POST /rol}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/rol/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
            @Valid @RequestBody RoleRequestDto roleRequestDto) {
        try {
            RoleResponseDto updatedRol = rolService.update(id, roleRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), updatedRol, "Rol updated successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.info("Error: {@PUT /rol/{id}}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        rolService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}
