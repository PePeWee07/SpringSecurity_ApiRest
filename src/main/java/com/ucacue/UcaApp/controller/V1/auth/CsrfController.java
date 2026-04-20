package com.ucacue.UcaApp.controller.V1.auth;

import java.util.Map;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Controlador de Cookie Oauth", description = "Genera cookie para el Oauth")
public class CsrfController {

    @GetMapping("/auth/csrf")
    @Operation(summary = "Crear Cookie", description = "Generar cookie para Login o Refresh")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "token", csrfToken.getToken(),
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName());
    }
}
