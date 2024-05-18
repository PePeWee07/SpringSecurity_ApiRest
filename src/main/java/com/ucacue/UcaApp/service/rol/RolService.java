package com.ucacue.UcaApp.service.rol;

import java.util.*;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;

public interface RolService {

    RolesEntity getMapperHelpRoleById(Long id);

    Optional<RoleResponseDto> getRoleById(Long id);
} 
