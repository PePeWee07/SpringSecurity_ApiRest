package com.ucacue.UcaApp.service.user;

import com.ucacue.UcaApp.model.dto.user.UserPreferenceRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserPreferenceResponseDto;

public interface UserPreferenceService {
    public UserPreferenceResponseDto getMyPreferences();
    public UserPreferenceResponseDto updateMyPreferences(UserPreferenceRequestDto request);
} 
