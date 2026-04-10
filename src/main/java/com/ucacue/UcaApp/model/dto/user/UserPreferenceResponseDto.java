package com.ucacue.UcaApp.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserPreferenceResponseDto {
    private Map<String, Object> settings;
}