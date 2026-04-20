package com.ucacue.UcaApp.controller.V1.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ucacue.UcaApp.model.dto.user.UserPreferenceRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserPreferenceResponseDto;
import com.ucacue.UcaApp.service.user.UserPreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/preferences")
@Tag(name = "Controlador Preferencias vizuales de Front End", description = "Configuraciones de prefrencias de usuarios en cuanto a vizualizacion en el Front End")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @GetMapping("/me")
    @Operation(summary = "Obtener preferencia", description = "Obtiene las preferencias del usaurio actual")
    public ResponseEntity<UserPreferenceResponseDto> getMyPreferences() {
        return ResponseEntity.ok(userPreferenceService.getMyPreferences());
    }

    @PatchMapping("/me")
    @Operation(summary = "Actualizar o guardar prefrencias", description = "Actualiza o guarda las prefrencias del usuario")
    public ResponseEntity<UserPreferenceResponseDto> updateMyPreferences(
            @RequestBody UserPreferenceRequestDto request) {
        return ResponseEntity.ok(userPreferenceService.updateMyPreferences(request));
    }
}
