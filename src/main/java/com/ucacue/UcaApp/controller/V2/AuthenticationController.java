package com.ucacue.UcaApp.controller.V2;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.exception.RoleNotFoundException;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.cliente.UserRequestDto;
import com.ucacue.UcaApp.service.user.impl.UserServiceImpl;
import com.ucacue.UcaApp.web.response.roleNotFound.RoleNotFoundResponse;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequestDto userRequest){
        try {
            if (userRequest.getRolesIds() == null || userRequest.getRolesIds().isEmpty()) {
                RoleNotFoundResponse response = new RoleNotFoundResponse(
                HttpStatus.NOT_FOUND.value(),
                List.of(Map.entry("error", "rolesIds not found in request")),
                "Role not found"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return new ResponseEntity<>(userServiceImpl.RegisterUser(userRequest), HttpStatus.CREATED);
        }catch (ConstraintViolationException ex) {
            throw ex;
        }catch (RoleNotFoundException e) {
            throw e;
        }catch (DataAccessException e) {
			throw e;
		}catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/log-in")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest userRequest){
        try {
            return new ResponseEntity<>(userServiceImpl.loginUser(userRequest), HttpStatus.OK);
        }  catch(BadCredentialsException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            Map<String, Object> responseGlobalExcp = new HashMap<>();
            responseGlobalExcp.put("Internal Server Error: ", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}