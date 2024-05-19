package com.ucacue.UcaApp.model.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.*;
import java.util.stream.Collectors;

import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.PermissionEntity;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.util.PermissionEntityFetcher;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(source = "permissionList", target = "permissionList")
    RoleResponseDto rolesEntityToRoleResponseDto(RolesEntity entity);

    default RolesEntity roleRequestDtoToRolesEntity(RoleRequestDto dto, @Context PermissionEntityFetcher permissionEntityFetcher) {
        RolesEntity entity = new RolesEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        Set<PermissionEntity> permissionEntities = permissionIdsToPermissionEntities(dto.getPermissionsIds(), permissionEntityFetcher);
        entity.setPermissionList(permissionEntities);
        return entity;
    }

    default Set<PermissionEntity> permissionIdsToPermissionEntities(Set<Long> permissionIds, PermissionEntityFetcher permissionEntityFetcher){
        return permissionIds.stream()
                      .map(id -> permissionEntityFetcher.mapPermissionIdToPermissionEntity(id)) // Usando permissionEntityFetcher para convertir ID a PermissionEntity
                      .collect(Collectors.toSet()); 
    }

    List<RoleResponseDto> rolesEntityListToRoleResponseDtoList(List<RolesEntity> entities);
}
