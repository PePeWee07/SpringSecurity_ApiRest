package com.ucacue.UcaApp.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.AdminUserManagerRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;

import java.util.List;

public interface AdminMangerService {

    //-----------Funciones para administrador de usuarios-----------

    AuthResponse loginUser(AuthLoginRequest authLoginRequest);

    AuthResponse RegisterUser(UserRequestDto userRequestDto);

    Authentication authenticate(String username, String password);

    AuthResponse refreshUserToken(String refreshToken);

    List<UserResponseDto> findAll();

    UserResponseDto getUserById(Long id);

    UserResponseDto findByEmail(String email);

    UserResponseDto findByEmailWithAuth(String email);

    UserResponseDto save(AdminUserManagerRequestDto userRequestDto);

    UserResponseDto update(Long id, AdminUserManagerRequestDto userRequestDto);

    boolean exists(Long id);

    Page<UserResponseDto> findAllWithFilters(UserResponseDto userResponseDto, Pageable pageable);
}
