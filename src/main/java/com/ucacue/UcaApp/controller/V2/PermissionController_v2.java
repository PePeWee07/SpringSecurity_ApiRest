package com.ucacue.UcaApp.controller.V2;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.model.dto.permission.PermissionRequestDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.service.permission.PermissionService;
import com.ucacue.UcaApp.web.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2")
public class PermissionController_v2 {

    @Autowired
    private PermissionService permissionService;

     @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(permissionService.findAll());
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/permission/{id}")
    public ResponseEntity<PermissionResponseDto> getPermissionById(@PathVariable Long id) {
        try {
            PermissionResponseDto response = permissionService.getPermissionById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            throw e;
        } 
    }

    @PostMapping("/permission")
    public ResponseEntity<?> create(@Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        try {
            PermissionResponseDto savedRol = permissionService.save(permissionRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedRol, "Permission created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (ConstraintViolationException ex) {
            throw ex;
        }catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/permission/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        try {
            PermissionResponseDto updatedRol = permissionService.update(id, permissionRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), updatedRol, "Permission updated successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (ConstraintViolationException ex) {
            throw ex;
        }catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
