package com.ucacue.UcaApp.model.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.*;

import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.util.MapperHelper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(source = "permissionList", target = "permissionList")
    RoleResponseDto rolesEntityToRoleResponseDto(RolesEntity entity);

    default RolesEntity roleRequestDtoToRolesEntity(RoleRequestDto dto) {
        RolesEntity entity = new RolesEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        
        return entity;
    }

    List<RoleResponseDto> rolesEntityListToRoleResponseDtoList(List<RolesEntity> entities);
}
