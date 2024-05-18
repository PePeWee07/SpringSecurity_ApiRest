package com.ucacue.UcaApp.service.rol.impl;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.RoleNotFoundException;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.model.mapper.RoleMapper;
import com.ucacue.UcaApp.repository.RolesRepository;
import com.ucacue.UcaApp.service.rol.RolService;

@Service
public class RolServiceImpl implements RolService{

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolesRepository rolesRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<RoleResponseDto> getRoleById(Long id) {
        return rolesRepository.findById(id)
            .map(roleMapper::rolesEntityToRoleResponseDto);
    }

    public RolesEntity getMapperHelpRoleById(Long id) {
        return rolesRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
    }
    
}