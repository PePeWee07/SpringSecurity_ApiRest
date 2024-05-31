package com.ucacue.UcaApp.controller.V2;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.service.user.impl.UserServiceImpl;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequestDto userRequest) {
        try {
            return new ResponseEntity<>(userServiceImpl.RegisterUser(userRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.info("Error: {@POST /Sign-up}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/log-in")
    public ResponseEntity<?> loginUser(@RequestBody @Valid AuthLoginRequest authLoginRequest) {
        try {
            AuthResponse response = userServiceImpl.loginUser(authLoginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("Error: {@POST /log-in}", e.getMessage());
            throw e;
        }
    }

}