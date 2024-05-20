package com.ucacue.UcaApp.service.permission;

import com.ucacue.UcaApp.model.dto.permission.PermissionRequestDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.model.entity.PermissionEntity;

import java.util.*;
public interface PermissionService {

    PermissionEntity getMapperHelpPermissionById(Long ind);

    List<PermissionResponseDto> findAll();

    PermissionResponseDto getPermissionById(Long id);

    PermissionResponseDto save(PermissionRequestDto permissionRequestDto);

    PermissionResponseDto update(Long id, PermissionRequestDto permissionRequestDto);

    boolean exists(Long id);

    public void deletePermissionById(Long id);
}
