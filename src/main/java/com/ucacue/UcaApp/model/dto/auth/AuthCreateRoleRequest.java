package com.ucacue.UcaApp.model.dto.auth;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record AuthCreateRoleRequest(
        @Size(max = 3, message = "The user cannot have more than 1 roles") List<String> roleListName) {
}