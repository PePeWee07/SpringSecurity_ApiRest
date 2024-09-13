package com.ucacue.UcaApp.controller.V2.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.web.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ucacue.UcaApp.model.dto.user.AdminUserManagerRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.service.admin.AdminMangerService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/manager")
@Tag(name = "AdminManagerController_v2", description = "Controlador para gestionar Usuarios")
public class AdminManagerController_v2 {

    private static final Logger logger = LoggerFactory.getLogger(AdminManagerController_v2.class);

    @Autowired
    private AdminMangerService adminMangerService;

    /* 
        ? Paginacion Basica
        * GET /users/page/0
        ? Paginación con tamaño de página personalizado
        * GET /users/page/0?pageSize=5
        ? Paginación con orden por un campo personalizado
        * GET /users/page/0?pageSize=10&sortBy=username
        ? Paginación con orden descendente
        * GET /users/page/0?pageSize=10&sortBy=username&direction=desc
        ? Filtrar por nombre
        * GET /users/page/0?name=John
    */
    @GetMapping("/users/page/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tabla de Usuarios", description = "Listado paginado de Usuarios.")
    public ResponseEntity<Page<UserResponseDto>> findAllWithPage(
        @PathVariable int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String dni,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String lastName) {

        Sort sort = Sort.by(sortBy);
        
        // Ajuste de la dirección de orden (ascendente o descendente)
        sort = "desc".equalsIgnoreCase(direction) ? sort.descending() : sort.ascending();
        
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        try {
            Page<UserResponseDto> userPage = adminMangerService.findAllWithFilters(name, lastName, email, dni, pageable);
            return ResponseEntity.ok(userPage);
        } catch (Exception e) {
            logger.error("Error al obtener la página de usuarios: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busqueda de Usuario por ID", description = "Obtiene los datos del Usuario.")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        try {
            UserResponseDto user = adminMangerService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.info("Error: {@GET /user/{id}}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busqueda de Usuario por EMAIL", description = "Obtiene los datos del Usuario.")
    public ResponseEntity<UserResponseDto> findByEmail(@PathVariable String email) {
        try {
            UserResponseDto user = adminMangerService.findByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.info("Error: {@GET /user/email/{email}}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Guardar Usuario", description = "Guardar datos de un Usuario.")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AdminUserManagerRequestDto userRequestDto) {
        try {
            UserResponseDto savedUser = adminMangerService.save(userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedUser, "User created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.info("Error: {@POST /user}", e.getMessage());
            throw e;
        }
    }

    @PatchMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualziar Usuario", description = "Actualiza los datos del Usuario.")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
            @Valid @RequestBody AdminUserManagerRequestDto userRequestDto) {
        try {
            UserResponseDto updatedUser = adminMangerService.update(id, userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.OK.value(), updatedUser, "User updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.info("Error: {@PATCH /user/{id}}", e.getMessage());
            throw e;
        }
    }
}
