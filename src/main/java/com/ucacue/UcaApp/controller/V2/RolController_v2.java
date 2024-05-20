package com.ucacue.UcaApp.controller.V2;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ucacue.UcaApp.web.response.roleandPermissionNotFound.RoleAndPermissionNotFoundResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2")
public class RolController_v2 {

    @Autowired
    private RolService rolService;

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(rolService.findAll());
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/rol/{id}")
    public ResponseEntity<RoleResponseDto> findById(@PathVariable Long id) {
        try {
            RoleResponseDto response = rolService.getRoleById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/rol")
    public ResponseEntity<?> create(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        if (roleRequestDto.getPermissionsIds() == null || roleRequestDto.getPermissionsIds().isEmpty()) {
            RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
                    HttpStatus.NOT_FOUND.value(),
                    List.of(Map.entry("error", "permissionsIds not found in request")),
                    "permissionsIds not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            RoleResponseDto savedRol = rolService.save(roleRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedRol, "Rol created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/rol/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RoleRequestDto roleRequestDto) {
        if (roleRequestDto.getPermissionsIds() == null || roleRequestDto.getPermissionsIds().isEmpty()) {
            RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
                    HttpStatus.NOT_FOUND.value(),
                    List.of(Map.entry("error", "permissionsIds not found in request")),
                    "Permission not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            RoleResponseDto updatedRol = rolService.update(id, roleRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), updatedRol, "Rol updated successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw e;
        } 
    }

    @DeleteMapping("role/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        rolService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}
