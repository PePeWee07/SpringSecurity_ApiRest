package com.ucacue.UcaApp.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(source = "permissionList", target = "permissionList")
    RoleResponseDto rolesEntityToRoleResponseDto(RolesEntity entity);

    List<RoleResponseDto> rolesEntityListToRoleResponseDtoList(List<RolesEntity> entities);
}
