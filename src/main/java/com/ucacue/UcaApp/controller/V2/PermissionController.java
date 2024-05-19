package com.ucacue.UcaApp.controller.V2;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.model.dto.permission.PermissionRequestDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.service.permission.PermissionService;

@RestController
@RequestMapping("/api/v2")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

     @GetMapping("/roles")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(permissionService.findAll());
        } catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/permission")
    public ResponseEntity<PermissionResponseDto> createPermission(@RequestBody PermissionRequestDto dto) {
        PermissionResponseDto response = permissionService.save(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("permission/{id}")
    public ResponseEntity<PermissionResponseDto> getPermissionById(@PathVariable Long id) {
        PermissionResponseDto response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("permission/{id}")
    public ResponseEntity<PermissionResponseDto> updatePermission(@PathVariable Long id, @RequestBody PermissionRequestDto dto) {
        PermissionResponseDto response = permissionService.update(id, dto);
        return ResponseEntity.ok(response);
    }
}
