package com.ucacue.UcaApp.controller.V2.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.web.response.ApiResponse;
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
public class AdminManagerController_v2 {

    private static final Logger logger = LoggerFactory.getLogger(AdminManagerController_v2.class);

    @Autowired
    private AdminMangerService adminMangerService;
    
    @GetMapping("/users/page/{page}")
    public ResponseEntity<Page<UserResponseDto>> findAllWithPage(@PathVariable int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        try {
            Page<UserResponseDto> userPage = adminMangerService.findAllForPage(pageable);
            return ResponseEntity.ok(userPage);
        } catch (Exception e) {
            logger.info("Error: {@GET /users/page/{page}}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user/{id}")
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
