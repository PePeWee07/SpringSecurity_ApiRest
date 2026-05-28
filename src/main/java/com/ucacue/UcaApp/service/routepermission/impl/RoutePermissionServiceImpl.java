package com.ucacue.UcaApp.service.routepermission.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.crud.RoutePermissionNotFoundException;
import com.ucacue.UcaApp.model.dto.routepermission.RoutePermissionResponseDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.RoutePermissionEntity;
import com.ucacue.UcaApp.model.mapper.RoutePermissionMapper;
import com.ucacue.UcaApp.repository.RoutePermissionRepository;
import com.ucacue.UcaApp.service.routepermission.RoutePermissionService;
import com.ucacue.UcaApp.util.authorities.RoleEntityFetcher;

@Service
public class RoutePermissionServiceImpl implements RoutePermissionService {

    @Autowired
    private RoutePermissionRepository routePermissionRepository;

    @Autowired
    private RoutePermissionMapper routePermissionMapper;

    @Autowired
    private RoleEntityFetcher roleEntityFetcher;

    @Transactional(readOnly = true)
    @Override
    public List<RoutePermissionResponseDto> findAll() {
        return routePermissionRepository.findAll()
                .stream()
                .map(routePermissionMapper::entityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RoutePermissionResponseDto assignRoles(Long id, Set<Long> roleIds) {
        RoutePermissionEntity entity = routePermissionRepository.findById(id)
                .orElseThrow(() -> new RoutePermissionNotFoundException(id));

        Set<RoleEntity> roles = roleIds == null
                ? new HashSet<>()
                : roleIds.stream()
                        .map(roleEntityFetcher::mapRoleIdToRolesEntity)
                        .collect(Collectors.toSet());

        entity.setRoleList(roles);
        entity = routePermissionRepository.save(entity);
        return routePermissionMapper.entityToResponse(entity);
    }

    @Transactional
    @Override
    public List<RoutePermissionResponseDto> syncPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return findAll();
        }

        List<RoutePermissionEntity> existing = routePermissionRepository.findByPathIn(paths);
        Set<String> existingPaths = existing.stream()
                .map(RoutePermissionEntity::getPath)
                .collect(Collectors.toSet());

        List<RoutePermissionEntity> toCreate = paths.stream()
                .distinct()
                .filter(path -> !existingPaths.contains(path))
                .map(path -> RoutePermissionEntity.builder()
                        .path(path)
                        .roleList(new HashSet<>())
                        .build())
                .collect(Collectors.toList());

        if (!toCreate.isEmpty()) {
            routePermissionRepository.saveAll(toCreate);
        }

        return findAll();
    }
}
