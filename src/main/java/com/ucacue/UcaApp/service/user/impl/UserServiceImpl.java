package com.ucacue.UcaApp.service.user.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.model.dto.auth.AuthLoginRequest;
import com.ucacue.UcaApp.model.dto.auth.AuthResponse;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.mapper.UserMapper;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.util.JwtUtils;
import com.ucacue.UcaApp.util.RoleEntityFetcher;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

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

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws BadCredentialsException {
    UserEntity userEntity = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadCredentialsException(email));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles()
            .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName()))));      
        userEntity.getRoles().stream()
            .flatMap(role -> role.getPermissionList().stream())
            .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));
        
        return new User(
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.isEnabled(),
            userEntity.isAccountNoExpired(),
            userEntity.isCredentialNoExpired(),
            userEntity.isAccountNoLocked(),
            authorityList
        );
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(username, "User loged succesfully", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid username or password"));
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
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
    public Optional<UserResponseDto> findById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toUserResponseDto);
    }

    @Transactional
    @Override
    public UserResponseDto save(UserRequestDto userRequestDto) {
        // Pasa el RoleEntityFetcher como argumento adicional a toUserEntity
        UserEntity userEntity = userMapper.toUserEntity(userRequestDto, roleEntityFetcher);
        userEntity = userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public AuthResponse RegisterUser(UserRequestDto userRequestDto) {
        UserEntity userEntity = userMapper.toUserEntity(userRequestDto, roleEntityFetcher);
        userEntity = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userEntity.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName()))));
        userEntity.getRoles().stream().flatMap(role -> role.getPermissionList().stream()).forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getEmail(), userEntity.getPassword(), authorities);
        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(userEntity.getEmail(), "User created successfully", accessToken, true);
        return authResponse;
    }

    @Transactional
    @Override
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        UserEntity userEntity = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User no encontrado con ID: " + id));
        // Pasa el RoleEntityFetcher como argumento adicional a toUserEntity
        userMapper.updateEntityFromDto(userRequestDto, userEntity, roleEntityFetcher);
        userEntity = userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
	@Transactional(readOnly=true)
	public Optional<UserResponseDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userMapper::toUserResponseDto);
	}

    //  PARA ALTA CONCURRENCIA DE USUARIOS REALIZANDO MUCHAS PETICIONES USAR CACHE
    // @Override
    // public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    //     return cache.computeIfAbsent(email, this::findUserByEmail);
    // }
    // private UserDetails findUserByEmail(String email) {
    //     UserEntity userEntity = userRepository.findByEmail(email)
    //         .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    //     return new org.springframework.security.core.userdetails.User(
    //         userEntity.getEmail(), userEntity.getPassword(), getAuthorities(userEntity.getRoles()));
    // }

}
