package com.ucacue.UcaApp.controller.V2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.web.response.ApiResponse;
import com.ucacue.UcaApp.web.response.roleNotFound.RoleNotFoundResponse;
import com.ucacue.UcaApp.exception.ResourceNotFound;
import com.ucacue.UcaApp.exception.RoleNotFoundException;
import com.ucacue.UcaApp.model.dto.cliente.UserRequestDto;
import com.ucacue.UcaApp.model.dto.cliente.UserResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/api/v2")
public class UserController_v2 {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController_v2.class);

    @GetMapping("/users")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/page/{page}")
    public ResponseEntity<?> findAllWithPage(@PathVariable int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        try {
            Page<UserResponseDto> userPage = userService.findAllForPage(pageable);
            return ResponseEntity.ok(userPage);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserResponseDto user = userService.findById(id)
                                                   .orElseThrow(() -> new ResourceNotFound("User not found with ID: " + id));

        try {
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error getting User by ID: {}", e.getMessage());
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        if (userRequestDto.getRolesIds() == null || userRequestDto.getRolesIds().isEmpty()) {
            RoleNotFoundResponse response = new RoleNotFoundResponse(
            HttpStatus.NOT_FOUND.value(),
            List.of(Map.entry("error", "rolesIds not found in request")),
            "Role not found"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            UserResponseDto savedUser = userService.save(userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), savedUser, "User created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (ConstraintViolationException ex) {
            throw ex;
        }catch (RoleNotFoundException e) {
            throw e;
        }catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage());
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) {
        if (!userService.exists(id)) {
            throw new ResourceNotFound("User not found with ID: " + id);
        }
        if (userRequestDto.getRolesIds() == null || userRequestDto.getRolesIds().isEmpty()) {
            RoleNotFoundResponse response = new RoleNotFoundResponse(
            HttpStatus.NOT_FOUND.value(),
            List.of(Map.entry("error", "rolesIds not found in request")),
            "Role not found"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            UserResponseDto updatedUser = userService.update(id, userRequestDto);
            ApiResponse response = new ApiResponse(HttpStatus.OK.value(), updatedUser, "User updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (RoleNotFoundException e) {
            throw e;
        } catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
            logger.error("Error updating User: {}", e.getMessage());
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (!userService.exists(id)) {
            throw new ResourceNotFound("User not found with ID: " + id);
        }
        try {
            userService.delete(id);            
            ApiResponse response = new ApiResponse(HttpStatus.OK.value(), "", "User deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error deleting User: {}", e.getMessage());
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
