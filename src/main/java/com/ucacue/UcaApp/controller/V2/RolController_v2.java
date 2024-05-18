package com.ucacue.UcaApp.controller.V2;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.exception.ResourceNotFound;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.service.rol.RolService;

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
    
}
