package com.ucacue.UcaApp.controller.V2.admin;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.model.dto.permission.PermissionRequestDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.service.permission.PermissionService;
import com.ucacue.UcaApp.web.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2")
public class PermissionController_v2 {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController_v2.class);

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(permissionService.findAll());
        } catch (Exception e) {
            logger.info("Error: {@GET /permissions}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/permission/{id}")
    public ResponseEntity<PermissionResponseDto> findById(@PathVariable Long id) {
        try {
            PermissionResponseDto response = permissionService.getPermissionById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("Error: {@GET /permission/{id}}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/permission")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        try {
            PermissionResponseDto savedRol = permissionService.save(permissionRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedRol,
                    "Permission created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.info("Error: {@POST /permission}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/permission/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
            @Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        try {
            PermissionResponseDto updatedRol = permissionService.update(id, permissionRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), updatedRol,
                    "Permission updated successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.info("Error: {@PUT /permission/{id}}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/permission/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermissionById(id);
        return ResponseEntity.noContent().build();
    }
}

