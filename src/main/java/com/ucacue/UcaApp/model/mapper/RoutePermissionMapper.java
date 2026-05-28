package com.ucacue.UcaApp.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ucacue.UcaApp.model.dto.routepermission.RoutePermissionResponseDto;
import com.ucacue.UcaApp.model.dto.routepermission.RouteRoleSummaryDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.RoutePermissionEntity;

@Mapper(componentModel = "spring")
public interface RoutePermissionMapper {

    RoutePermissionMapper INSTANCE = Mappers.getMapper(RoutePermissionMapper.class);

    RoutePermissionResponseDto entityToResponse(RoutePermissionEntity entity);

    RouteRoleSummaryDto roleEntityToSummary(RoleEntity role);
}
