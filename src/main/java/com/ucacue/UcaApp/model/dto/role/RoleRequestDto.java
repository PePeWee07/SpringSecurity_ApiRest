package com.ucacue.UcaApp.model.dto.role;

import lombok.*;
import java.util.*;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {
    
    private Long id;

    private String name;

    @NotEmpty(message = "permissionsIds cannot be empty")
    private Set<Long> permissionsIds;
}
