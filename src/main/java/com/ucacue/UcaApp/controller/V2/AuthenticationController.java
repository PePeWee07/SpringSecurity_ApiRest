package com.ucacue.UcaApp.controller.V2;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.auth.AuthCreateUserRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;

import com.ucacue.UcaApp.service.user.impl.UserServiceImpl;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthCreateUserRequest userRequest){
        return new ResponseEntity<>(userServiceImpl.createUser(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest userRequest){
        return new ResponseEntity<>(userServiceImpl.loginUser(userRequest), HttpStatus.OK);
    }
}