package com.ucacue.UcaApp.model.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import java.util.*;
import java.util.stream.Collectors;

import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.PermissionEntity;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.util.PermissionEntityFetcher;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(source = "permissionList", target = "permissionList")
    RoleResponseDto rolesEntityToRoleResponseDto(RoleEntity entity);

    default RoleEntity roleRequestDtoToRolesEntity(RoleRequestDto dto,
            @Context PermissionEntityFetcher permissionEntityFetcher) {
        RoleEntity entity = new RoleEntity();
        entity.setName(dto.getName());

        Set<PermissionEntity> permissionEntities = permissionIdsToPermissionEntities(dto.getPermissionsIds(),
                permissionEntityFetcher);
        entity.setPermissionList(permissionEntities);
        return entity;
    }

    // Metodo para convertir una lista de ids de permisos a una lista de entidades de permisos
    default Set<PermissionEntity> permissionIdsToPermissionEntities(Set<Long> permissionIds,
            PermissionEntityFetcher permissionEntityFetcher) {
        return permissionIds.stream()
                .map(id -> permissionEntityFetcher.mapPermissionIdToPermissionEntity(id))
                .collect(Collectors.toSet());
    }

    default void updateEntityFromDto(RoleRequestDto dto, @MappingTarget RoleEntity entity,
            @Context PermissionEntityFetcher permissionEntityFetcher) {
        if (dto.getName() != null)
            entity.setName(dto.getName());
        if (dto.getPermissionsIds() != null)
            entity.setPermissionList(
                    permissionIdsToPermissionEntities(dto.getPermissionsIds(), permissionEntityFetcher));
    }
}
