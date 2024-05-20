package com.ucacue.UcaApp.model.dto.role;

import lombok.*;

import java.io.Serializable;
import java.util.*;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long id;

    private String name;

    @NotEmpty(message = "permissionsIds cannot be empty")
    private Set<Long> permissionsIds;
}
