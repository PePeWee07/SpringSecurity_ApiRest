package com.ucacue.UcaApp.service.user.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.model.dto.cliente.UserRequestDto;
import com.ucacue.UcaApp.model.dto.cliente.UserResponseDto;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.mapper.UserMapper;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.util.MapperHelper;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MapperHelper mapperHelper; 

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws BadCredentialsException {

        UserEntity userEntity = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                throw new BadCredentialsException("The user " + email + " does not exist.");
            });

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
        // Pasa el mapperHelper como argumento adicional a toUserEntity
        UserEntity userEntity = userMapper.toUserEntity(userRequestDto, mapperHelper);
        userEntity = userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        UserEntity userEntity = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User no encontrado con ID: " + id));
        // Pasa el mapperHelper como argumento adicional a toUserEntity
        userMapper.updateEntityFromDto(userRequestDto, userEntity, mapperHelper);
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
