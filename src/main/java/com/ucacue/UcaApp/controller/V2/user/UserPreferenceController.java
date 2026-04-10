package com.ucacue.UcaApp.controller.V2.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.model.dto.user.UserPreferenceRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserPreferenceResponseDto;
import com.ucacue.UcaApp.service.user.UserPreferenceService;

@RestController
@RequestMapping("/api/v2/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @GetMapping("/me")
    public ResponseEntity<UserPreferenceResponseDto> getMyPreferences() {
        return ResponseEntity.ok(userPreferenceService.getMyPreferences());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserPreferenceResponseDto> updateMyPreferences(
            @RequestBody UserPreferenceRequestDto request) {
        return ResponseEntity.ok(userPreferenceService.updateMyPreferences(request));
    }
}
