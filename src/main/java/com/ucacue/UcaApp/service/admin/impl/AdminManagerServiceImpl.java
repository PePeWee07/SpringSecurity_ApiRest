package com.ucacue.UcaApp.service.admin.impl;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.exception.auth.UserNotFoundAuthException;
import com.ucacue.UcaApp.exception.crud.UserAlreadyExistsException;
import com.ucacue.UcaApp.exception.crud.UserNotFoundException;
import com.ucacue.UcaApp.model.dto.Api.ApiResponse;
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.ManagerUserRequestDto;
import com.ucacue.UcaApp.model.dto.user.ManagerUsersResponseDto;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.RefreshTokenEntity;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.mapper.UserMapper;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.admin.AdminMangerService;
import com.ucacue.UcaApp.service.token.TokenService;
import com.ucacue.UcaApp.util.RoleEntityFetcher;
import com.ucacue.UcaApp.util.UserSpecificationFilter;
import com.ucacue.UcaApp.util.UserStatusValidator;
import com.ucacue.UcaApp.util.token.JwtUtils;
import com.ucacue.UcaApp.util.token.PasswordEncoderUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;

@Service
public class AdminManagerServiceImpl implements AdminMangerService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleEntityFetcher roleEntityFetcher;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private AuditorAware<String> auditorAware;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByEmailWithAuth(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::mapToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundAuthException("Invalid credentials"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundAuthException("Invalid credentials"));

        return new User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isAccountNonLocked(),
                userEntity.isCredentialsNonExpired(),
                userEntity.getAuthorities());
        } catch (UserNotFoundException e) {
            throw e;
        }
        
    }

    @Transactional
    @Override
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByEmailWithLock(username)
                .orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.SearchType.EMAIL));

        // Límite de sesiones activas
        tokenService.limitSession(username);

        // Crear tokens
        String accessToken = jwtUtils.createToken(authentication);
        String refreshToken = jwtUtils.createRefreshToken(authentication);
        
        DecodedJWT decoded = jwtUtils.validateToken(refreshToken);
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setJti(decoded.getId());
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setRevoked(false);
        refreshTokenEntity.setExpiresAt(jwtUtils.getExpirationDate(refreshToken));

        tokenService.saveTokenRefresh(refreshTokenEntity);

        return new AuthResponse(username, "User logged in successfully", accessToken, refreshToken, true);
    }

    @Transactional
    @Override
    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        UserStatusValidator.validate(userDetails); // Validar el estado del usuario

        passwordEncoderUtil.verifyPassword(password, userDetails.getPassword());

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    @Transactional
    @Override
    public ApiResponse RegisterUser(UserRequestDto userRequestDto) {
        UserEntity userEntity = userMapper.mapToUserEntity(userRequestDto, passwordEncoderUtil);

        // Set the account states to true
        userEntity.setEnabled(false);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);

        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new UserAlreadyExistsException( "Email: " + userEntity.getEmail() + " already exists");
        }

        if (userRepository.existsByDni(userEntity.getDni())) {
            throw new UserAlreadyExistsException("DNI: " + userEntity.getDni() + " already exists");
        }

        // Asignar el rol por defecto "USER" (ID 2)
        RoleEntity defaultRole = roleEntityFetcher.mapRoleIdToRolesEntity(2L);
        Set<RoleEntity> roles = new HashSet<>(userEntity.getRoles());
        roles.add(defaultRole);
        userEntity.setRoles(roles);

        // Establecer el auditor manualmente si no está autenticado
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        if (currentAuditor.isEmpty()) {
            userEntity.setCreatedBy(userEntity.getEmail());
        }

        userEntity = userRepository.save(userEntity);

        ApiResponse response = new ApiResponse(201, userEntity.getEmail(), "User created successfully");
        return response;
    }

    

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::mapToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::mapToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException(id, UserNotFoundException.SearchType.ID));
    }

    @Transactional
    @Override
    public UserResponseDto save(ManagerUserRequestDto userRequestDto) {
        UserEntity userEntity = userMapper.mapToAdminUserEntity(userRequestDto, roleEntityFetcher, passwordEncoderUtil);
        userEntity = userRepository.save(userEntity);
        return userMapper.mapToUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public UserResponseDto update(Long id, ManagerUserRequestDto userRequestDto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id, UserNotFoundException.SearchType.ID));
        userMapper.updateAdminUserEntityFromDto(userRequestDto, userEntity, roleEntityFetcher, passwordEncoderUtil);
        userEntity = userRepository.save(userEntity);
        return userMapper.mapToUserResponseDto(userEntity);
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
                .map(userMapper::mapToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException(email, UserNotFoundException.SearchType.EMAIL));
    }

    @Override
    @Transactional
    public Page<ManagerUsersResponseDto> findAllWithFilters(UserResponseDto users, Pageable pageable) {
       
        // Obtener los campos no nulos del objeto UserResponseDto
        Map<String, Object> filters = new HashMap<>();
        for (Field field : UserResponseDto.class.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) //* Ignorar campos estaticos
                continue;
            field.setAccessible(true); //* Hacer accesibles los campos privados
            try {
                Object value = field.get(users); 

                // Solo agregar si tiene valor y NO es una lista
                if (value != null && !value.toString().isEmpty() && !(value instanceof List)) {
                    filters.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Specification<UserEntity> spec = UserSpecificationFilter.filterUsers(filters);
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(userMapper::mapToManagerResponseDto);
    }

}