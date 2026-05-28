package com.ucacue.UcaApp.service.routepermission;

import java.util.List;
import java.util.Set;

import com.ucacue.UcaApp.model.dto.routepermission.RoutePermissionResponseDto;

public interface RoutePermissionService {

    List<RoutePermissionResponseDto> findAll();

    RoutePermissionResponseDto assignRoles(Long id, Set<Long> roleIds);

    List<RoutePermissionResponseDto> syncPaths(List<String> paths);
}
