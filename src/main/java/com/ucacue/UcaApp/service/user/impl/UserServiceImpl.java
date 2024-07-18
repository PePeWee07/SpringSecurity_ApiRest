package com.ucacue.UcaApp.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ucacue.UcaApp.exception.crud.UserNotFoundException;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.mapper.UserMapper;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.user.UserService;
import com.ucacue.UcaApp.util.token.JwtUtils;
import com.ucacue.UcaApp.util.token.PasswordEncoderUtil;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Transactional
    @Override
    public UserResponseDto getUserProfile(String token) {
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String userEmail = jwtUtils.getUsernameFromToken(decodedJWT);
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail, UserNotFoundException.SearchType.EMAIL));
        return userMapper.mapToUserResponseDto(userEntity);
    }

    @Transactional
    @Override
    public UserResponseDto editUserProfile(String token, UserRequestDto userRequestDto) {
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String userEmail = jwtUtils.getUsernameFromToken(decodedJWT);
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail, UserNotFoundException.SearchType.EMAIL));

        userMapper.updateEntityFromDtoUserProfile(userRequestDto, userEntity, passwordEncoderUtil);
        userEntity = userRepository.save(userEntity);
        return userMapper.mapToUserResponseDto(userEntity);
    }

}
