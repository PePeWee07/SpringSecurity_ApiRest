package com.ucacue.UcaApp.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.cliente.UserRequestDto;
import com.ucacue.UcaApp.model.dto.cliente.UserResponseDto;

import java.util.Optional;
import java.util.List;

public interface UserService {

    List<UserResponseDto> findAll();

    Page<UserResponseDto> findAllForPage(Pageable pageable);

    Optional<UserResponseDto> findById(Long id);
    
    Optional<UserResponseDto> findByEmail(String email);

    UserResponseDto save(UserRequestDto userRequestDto);
    AuthResponse saveUser(UserRequestDto userRequestDto);

    UserResponseDto update(Long id, UserRequestDto userRequestDto);

    void delete(Long id);

    boolean exists(Long id);
    
}
