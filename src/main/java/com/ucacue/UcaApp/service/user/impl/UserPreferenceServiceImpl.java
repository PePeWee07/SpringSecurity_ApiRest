package com.ucacue.UcaApp.service.user.impl;

import java.util.LinkedHashMap;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucacue.UcaApp.model.dto.user.UserPreferenceRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserPreferenceResponseDto;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.model.entity.UserPreferenceEntity;
import com.ucacue.UcaApp.repository.UserPreferenceRepository;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.user.UserPreferenceService;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserPreferenceServiceImpl(
        UserPreferenceRepository userPreferenceRepository,
        UserRepository userRepository,
        ObjectMapper objectMapper) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserPreferenceResponseDto getMyPreferences() {
        UserEntity user = getAuthenticatedUser();

        UserPreferenceEntity preference = userPreferenceRepository.findByUserId(user.getId()).orElse(null);

        if (preference == null || preference.getSettingsJson() == null) {
            return new UserPreferenceResponseDto(new LinkedHashMap<>());
        }

        return new UserPreferenceResponseDto(preference.getSettingsJson());
    }

    @Transactional
    @Override
    public UserPreferenceResponseDto updateMyPreferences(UserPreferenceRequestDto request) {
        UserEntity user = getAuthenticatedUser();

        UserPreferenceEntity preference = userPreferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreferenceEntity entity = new UserPreferenceEntity();
                    entity.setUser(user);
                    entity.setSettingsJson(new LinkedHashMap<>());
                    return entity;
                });

        preference.setSettingsJson(
                request.getSettings() != null ? request.getSettings() : new LinkedHashMap<>());

        UserPreferenceEntity saved = userPreferenceRepository.save(preference);

        return new UserPreferenceResponseDto(saved.getSettingsJson());
    }

    private UserEntity getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
    
}
