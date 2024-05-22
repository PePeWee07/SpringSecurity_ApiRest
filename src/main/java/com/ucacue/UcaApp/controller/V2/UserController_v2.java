package com.ucacue.UcaApp.controller.V2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.web.response.ApiResponse;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/api/v2")
public class UserController_v2 {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> findAll() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/users/page/{page}")
    public ResponseEntity<Page<UserResponseDto>> findAllWithPage(@PathVariable int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        try {
            Page<UserResponseDto> userPage = userService.findAllForPage(pageable);
            return ResponseEntity.ok(userPage);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        try {
            UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<UserResponseDto> findByEmail(@PathVariable String email) {
        try {
            UserResponseDto user = userService.findByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto savedUser = userService.save(userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedUser, "User created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto updatedUser = userService.update(id, userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.OK.value(), updatedUser, "User updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw e;
        }
    }
}
