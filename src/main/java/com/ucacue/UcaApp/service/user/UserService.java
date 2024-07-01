package com.ucacue.UcaApp.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {

    AuthResponse loginUser(AuthLoginRequest authLoginRequest);

    Authentication authenticate(String username, String password);

    AuthResponse RegisterUser(UserRequestDto userRequestDto);

    AuthResponse refreshUserToken(String refreshToken);

    List<UserResponseDto> findAll();

    Page<UserResponseDto> findAllForPage(Pageable pageable);

    UserResponseDto getUserById(Long id);

    UserResponseDto findByEmail(String email);

    UserResponseDto findByEmailWithAuth(String email);

    UserResponseDto save(UserRequestDto userRequestDto);

    UserResponseDto update(Long id, UserRequestDto userRequestDto);

    void delete(Long id);

    boolean exists(Long id);
}
