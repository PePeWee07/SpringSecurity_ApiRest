package com.ucacue.UcaApp.model.dto.user;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserPreferenceRequestDto {
    private Map<String, Object> settings;
}