package com.ucacue.UcaApp.service.rol.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.exception.RoleNotFoundException;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.repository.RolesRepository;
import com.ucacue.UcaApp.service.rol.RolService;

@Service
public class RolServiceImpl implements RolService{

    @Autowired
    private RolesRepository rolesRepository;

    public RolesEntity getRoleById(Long id) {
        return rolesRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
    }
    
}