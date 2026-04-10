package com.ucacue.UcaApp.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthTokensResult;
import com.ucacue.UcaApp.model.dto.user.ManagerUserRequestDto;
import com.ucacue.UcaApp.model.dto.user.ManagerUsersResponseDto;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;

import java.util.List;

public interface AdminMangerService {

    //-----------Funciones para administrador de usuarios-----------

    AuthTokensResult loginUser(AuthLoginRequest authLoginRequest);

    ApiResponse RegisterUser(UserRequestDto userRequestDto);

    Authentication authenticate(String username, String password);

    List<UserResponseDto> findAll();

    UserResponseDto getUserById(Long id);

    UserResponseDto findByEmail(String email);

    UserResponseDto findByEmailWithAuth(String email);

    UserResponseDto save(ManagerUserRequestDto userRequestDto);

    UserResponseDto update(Long id, ManagerUserRequestDto userRequestDto);

    boolean exists(Long id);

    Page<ManagerUsersResponseDto> findAllWithFilters(UserResponseDto userResponseDto, Pageable pageable);
}
