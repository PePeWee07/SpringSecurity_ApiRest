package com.ucacue.UcaApp.util;

import org.springframework.stereotype.Component;

import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.service.rol.RolService;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class RoleEntityFetcher {

    @Autowired
    private RolService rolService;

    public RoleEntity mapRoleIdToRolesEntity(Long id) {
        return rolService.getMapperFetcherRoleById(id);
    }
}
