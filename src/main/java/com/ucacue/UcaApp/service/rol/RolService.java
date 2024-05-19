package com.ucacue.UcaApp.service.rol;

import java.util.*;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;

public interface RolService {

    RolesEntity getMapperHelpRoleById(Long id);

    List<RoleResponseDto> findAll();

    Optional<RoleResponseDto> getRoleById(Long id);

    RoleResponseDto save(RoleRequestDto roleRequestDto);

    RoleResponseDto update(Long id, RoleRequestDto roleRequestDto);

    boolean exists(Long id);

} 
