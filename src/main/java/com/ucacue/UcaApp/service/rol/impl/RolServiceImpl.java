package com.ucacue.UcaApp.service.rol.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.exception.crud.RoleNotFoundException;
import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.mapper.RoleMapper;
import com.ucacue.UcaApp.repository.RoleRepository;
import com.ucacue.UcaApp.service.rol.RolService;
import com.ucacue.UcaApp.util.PermissionEntityFetcher;

@Service
public class RolServiceImpl implements RolService{

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionEntityFetcher permissionEntityFetcher; 

    @Transactional(readOnly = true)
    @Override
    public RoleEntity getMapperFetcherRoleById(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponseDto> findAll() {
        return roleRepository.findAll()
            .stream()
            .map(roleMapper::rolesEntityToRoleResponseDto)
            .collect(Collectors.toList());
    }        

    @Transactional(readOnly = true)
    @Override
    public RoleResponseDto getRoleById(Long id) {
        return roleRepository.findById(id)
            .map(roleMapper::rolesEntityToRoleResponseDto)
            .orElseThrow(() -> new RoleNotFoundException(id));
    }        

    @Transactional
    @Override
    public RoleResponseDto save(RoleRequestDto roleRequestDto) {
        RoleEntity rolesEntity = roleMapper.roleRequestDtoToRolesEntity(roleRequestDto, permissionEntityFetcher);
        rolesEntity = roleRepository.save(rolesEntity);
        return roleMapper.rolesEntityToRoleResponseDto(rolesEntity);
    }

    @Transactional
    @Override
    public RoleResponseDto update(Long id, RoleRequestDto roleRequestDto) {
        RoleEntity rolesEntity = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
        roleMapper.upddateEntityFromDto(roleRequestDto, rolesEntity, permissionEntityFetcher);
        rolesEntity = roleRepository.save(rolesEntity);
        return roleMapper.rolesEntityToRoleResponseDto(rolesEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(Long id) {
        return roleRepository.existsById(id);
    }

    @Override
    public void deleteRoleById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRoleById'");
    }
}
