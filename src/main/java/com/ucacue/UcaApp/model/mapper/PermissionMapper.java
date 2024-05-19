package com.ucacue.UcaApp.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.ucacue.UcaApp.model.dto.permission.PermissionRequestDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.model.entity.PermissionEntity;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    // Mapea de PermissionEntity a PermissionResponseDto
    PermissionResponseDto permissionEntityToPermissionResponseDto(PermissionEntity entity);

    // Mapea de PermissionRequestDto a PermissionEntity
    PermissionEntity permissionRequestDtoToPermissionEntity(PermissionRequestDto dto);

    // Actualiza una entidad existente desde un DTO
    void updateEntityFromDto(PermissionRequestDto dto, @MappingTarget PermissionEntity entity);
}
