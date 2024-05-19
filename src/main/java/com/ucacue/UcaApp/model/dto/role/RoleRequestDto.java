package com.ucacue.UcaApp.model.dto.role;

import lombok.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {
    
    private Long id;

    private String name;

    private Set<Long> permissionsIds;
}
