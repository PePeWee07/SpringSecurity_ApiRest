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
import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.AdminUserManagerRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.mapper.UserMapper;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.admin.AdminMangerService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;

@Service
public class AdminManagerServiceImpl implements AdminMangerService, UserDetailsService {

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

    @Autowired
    private AuditorAware<String> auditorAware;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByEmailWithAuth(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::mapToUserResponseDto)
                .orElseThrow(() -> new UserNotFoundAuthException("Invalid username or password"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundAuthException("Invalid username or password"));

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

        String accessToken = jwtUtils.createToken(authentication);
        String refreshToken = jwtUtils.createRefreshToken(authentication);
        return new AuthResponse(username, "User logged in successfully", accessToken, refreshToken, true);
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
            UserEntity userEntity = userMapper.mapToUserEntity(userRequestDto, passwordEncoderUtil);

            // Set the account states to true
            userEntity.setEnabled(true);
            userEntity.setAccountNonExpired(true);
            userEntity.setAccountNonLocked(true);
            userEntity.setCredentialsNonExpired(true);

            if (userRepository.existsByEmail(userEntity.getEmail())) {
                throw new UserAlreadyExistsException(userEntity.getEmail());
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

            Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getEmail(), userEntity.getPassword(), userEntity.getAuthorities());
            
            String accessToken = jwtUtils.createToken(authentication);
            String refreshToken = jwtUtils.createRefreshToken(authentication);

            return new AuthResponse(userEntity.getEmail(), "User created successfully", accessToken, refreshToken, true);
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Transactional
    @Override
    public AuthResponse refreshUserToken(String refreshToken) {
        DecodedJWT decodedJWT = jwtUtils.validateToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(decodedJWT);

        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = jwtUtils.createToken(authentication);
        String newRefreshToken = jwtUtils.createRefreshToken(authentication);

        return new AuthResponse(username, "Token refreshed successfully", newAccessToken, newRefreshToken, true);
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
    public UserResponseDto save(AdminUserManagerRequestDto userRequestDto) {
        UserEntity userEntity = userMapper.mapToAdminUserEntity(userRequestDto, roleEntityFetcher, passwordEncoderUtil);
        userEntity = userRepository.save(userEntity);
        return userMapper.mapToUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public UserResponseDto update(Long id, AdminUserManagerRequestDto userRequestDto) {
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
    public Page<UserResponseDto> findAllWithFilters(UserResponseDto userResponseDto, Pageable pageable) {
       
        // Obtener los campos no nulos del objeto UserResponseDto
        Map<String, Object> filters = new HashMap<>();
        for (Field field : UserResponseDto.class.getDeclaredFields()) {
            field.setAccessible(true); //* Hacer accesibles los campos privados
            try {
                Object value = field.get(userResponseDto); 

                if (value != null && !value.toString().isEmpty()) {
                    filters.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Specification<UserEntity> spec = UserSpecificationFilter.filterUsers(filters);
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(userMapper::mapToUserResponseDto);
    }

}