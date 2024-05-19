package com.ucacue.UcaApp.controller.V2;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.exception.PermissionNotFoundException;
import com.ucacue.UcaApp.exception.ResourceNotFound;
import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.service.rol.RolService;
import com.ucacue.UcaApp.web.response.ApiResponse;
import com.ucacue.UcaApp.web.response.roleandPermissionNotFound.RoleAndPermissionNotFoundResponse;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2")
public class RolController_v2 {

    @Autowired
    private RolService rolService;

    @GetMapping("/rol/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        RoleResponseDto user = rolService.getRoleById(id)
            .orElseThrow(() -> new ResourceNotFound("Rol not found with ID: " + id));

        try {
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/rol")
    public ResponseEntity<?> create(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        if (roleRequestDto.getPermissionsIds() == null || roleRequestDto.getPermissionsIds().isEmpty()) {
            RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
            HttpStatus.NOT_FOUND.value(),
            List.of(Map.entry("error", "permissionsIds not found in request")),
            "Role not found"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            RoleResponseDto savedRol = rolService.save(roleRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedRol, "Rol created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (ConstraintViolationException ex) {
            throw ex;
        }catch (PermissionNotFoundException e) {
            throw e;
        }catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/rol/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RoleRequestDto roleRequestDto) {
        if (!rolService.exists(id)) {
            throw new ResourceNotFound("Rol not found with ID: " + id);
        }
        if (roleRequestDto.getPermissionsIds() == null || roleRequestDto.getPermissionsIds().isEmpty()) {
            RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
            HttpStatus.NOT_FOUND.value(),
            List.of(Map.entry("error", "permissionsIds not found in request")),
            "Role not found"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            RoleResponseDto updatedRol = rolService.update(id, roleRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), updatedRol, "Rol updated successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (ConstraintViolationException ex) {
            throw ex;
        }catch (PermissionNotFoundException e) {
            throw e;
        }catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
