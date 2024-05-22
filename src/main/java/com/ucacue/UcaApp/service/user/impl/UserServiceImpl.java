package com.ucacue.UcaApp.service.user.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.auth.UserAlreadyExistsException;
import com.ucacue.UcaApp.exception.auth.UserNotFoundException;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.mapper.UserMapper;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.util.JwtUtils;
import com.ucacue.UcaApp.util.PasswordEncoderUtil;
import com.ucacue.UcaApp.util.RoleEntityFetcher;
import com.ucacue.UcaApp.util.UserStatusValidator;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleEntityFetcher roleEntityFetcher;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email, UserNotFoundException.SearchType.EMAIL));

        return new User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                userEntity.getAuthorities());
    }

    @Transactional
    @Override
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponse(username, "User logged in successfully", accessToken, true);
    }

    @Transactional
    @Override
    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        UserStatusValidator.validate(userDetails); // Validar el estado del usuario

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    @Transactional
    @Override
    public AuthResponse RegisterUser(UserRequestDto userRequestDto) {
        try {
            UserEntity userEntity = userMapper.toUserEntity(userRequestDto, roleEntityFetcher, passwordEncoderUtil);

            if (userRepository.existsByEmail(userEntity.getEmail())) {
                throw new UserAlreadyExistsException(userEntity.getEmail());
            }

            userEntity = userRepository.save(userEntity);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userEntity.getEmail(), userEntity.getPassword(), userEntity.getAuthorities());

            String accessToken = jwtUtils.createToken(authentication);

            return new AuthResponse(userEntity.getEmail(), "User created successfully", accessToken, true);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponseDto> findAllForPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDto);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException(id, UserNotFoundException.SearchType.ID));
    }

    @Transactional
    @Override
    public UserResponseDto save(UserRequestDto userRequestDto) {
        UserEntity userEntity = userMapper.toUserEntity(userRequestDto, roleEntityFetcher, passwordEncoderUtil);
        userEntity = userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id, UserNotFoundException.SearchType.ID));
        userMapper.updateEntityFromDto(userRequestDto, userEntity, roleEntityFetcher, passwordEncoderUtil);
        userEntity = userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException(email, UserNotFoundException.SearchType.EMAIL));
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    // PARA ALTA CONCURRENCIA DE USUARIOS REALIZANDO MUCHAS PETICIONES USAR CACHE
    // @Override
    // public UserDetails loadUserByUsername(String email) throws
    // UsernameNotFoundException {
    // return cache.computeIfAbsent(email, this::findUserByEmail);
    // }
    // private UserDetails findUserByEmail(String email) {
    // UserEntity userEntity = userRepository.findByEmail(email)
    // .orElseThrow(() -> new UsernameNotFoundException("User not found with email:
    // " + email));
    // return new org.springframework.security.core.userdetails.User(
    // userEntity.getEmail(), userEntity.getPassword(),
    // getAuthorities(userEntity.getRoles()));
    // }

}
