package com.ucacue.UcaApp.model.dto.auth;

import java.util.Date;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record AuthCreateUserRequest(
        @NotBlank String name,
        @NotBlank String lastName,
        @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String address,
        @NotBlank String dni,
        @NotBlank String password,
         Date creationDate,
                                    @Valid AuthCreateRoleRequest roleRequest) {
}
