package com.ucacue.UcaApp.service.user;

import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;

public interface UserService {
    
    UserResponseDto getUserProfile(String token);
    UserResponseDto editUserProfile(String token, UserRequestDto userRequestDto);
    
}
