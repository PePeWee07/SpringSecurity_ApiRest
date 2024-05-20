package com.ucacue.UcaApp.service.rol;

import java.util.*;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;

public interface RolService {

    RoleEntity getMapperFetcherRoleById(Long id);

    List<RoleResponseDto> findAll();

    RoleResponseDto getRoleById(Long id);

    RoleResponseDto save(RoleRequestDto roleRequestDto);

    RoleResponseDto update(Long id, RoleRequestDto roleRequestDto);

    boolean exists(Long id);

    public void deleteRoleById(Long id);

} 
