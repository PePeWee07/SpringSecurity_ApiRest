package com.ucacue.UcaApp.controller.V2.user;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.web.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v2/user")
@Tag(name = "UserController2_v2", description = "Controlador de Usuario")
public class UserController2_v2 {

    private static final Logger logger = LoggerFactory.getLogger(UserController2_v2.class);
    
    @Autowired
    private UserService userService;

   @GetMapping("/profile")
   @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
   @Operation(summary = "Ver Perfil", description = "Accede al perfil del Usuario con su propio token.")
    public ResponseEntity<UserResponseDto> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            UserResponseDto user = userService.getUserProfile(actualToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.info("Error: {@GET /user/profile}", e.getMessage());
            throw e;
        }
    }

    @PatchMapping("/editProfile")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    @Operation(summary = "Actualizar Perfil", description = "Actualiza al perfil del Usuario con su propio token.")
    public ResponseEntity<ApiResponse> updateProfile(@RequestHeader("Authorization") String token, @Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            String actualToken = token.replace("Bearer ", "");
            UserResponseDto updatedUser = userService.editUserProfile(actualToken, userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.OK.value(), updatedUser, "User updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.info("Error: {@PATCH /user/editProfile}", e.getMessage());
            throw e;
        }
    }

}
