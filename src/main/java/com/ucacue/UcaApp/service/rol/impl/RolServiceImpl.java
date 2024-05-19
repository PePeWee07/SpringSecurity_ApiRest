package com.ucacue.UcaApp.service.rol.impl;



import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.RoleNotFoundException;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.dto.role.RoleRequestDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.model.mapper.RoleMapper;
import com.ucacue.UcaApp.repository.RolesRepository;
import com.ucacue.UcaApp.service.rol.RolService;
import com.ucacue.UcaApp.util.PermissionEntityFetcher;

@Service
public class RolServiceImpl implements RolService{

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PermissionEntityFetcher mapperHelper2; 

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponseDto> findAll() {
        return rolesRepository.findAll()
            .stream()
            .map(roleMapper::rolesEntityToRoleResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RoleResponseDto> getRoleById(Long id) {
        return rolesRepository.findById(id)
            .map(roleMapper::rolesEntityToRoleResponseDto);
    }

    @Transactional(readOnly = true)
    @Override
    public RolesEntity getMapperHelpRoleById(Long id) {
        return rolesRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Transactional
    @Override
    public RoleResponseDto save(RoleRequestDto roleRequestDto) {
        RolesEntity rolesEntity = roleMapper.roleRequestDtoToRolesEntity(roleRequestDto, mapperHelper2);
        rolesEntity = rolesRepository.save(rolesEntity);
        return roleMapper.rolesEntityToRoleResponseDto(rolesEntity);
    }

    @Transactional
    @Override
    public RoleResponseDto update(Long id, RoleRequestDto roleRequestDto) {
        RolesEntity rolesEntity = rolesRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
        roleMapper.upddateEntityFromDto(roleRequestDto, rolesEntity, mapperHelper2);
        rolesEntity = rolesRepository.save(rolesEntity);
        return roleMapper.rolesEntityToRoleResponseDto(rolesEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(Long id) {
        return rolesRepository.existsById(id);
    }
    
}