package com.ucacue.UcaApp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ucacue.UcaApp.model.entity.PermissionEntity;
import com.ucacue.UcaApp.service.permission.PermissionService;

@Component
public class PermissionEntityFetcher {

    @Autowired
    private PermissionService permissionService;

    public PermissionEntity mapPermissionIdToPermissionEntity(Long id) {
        return permissionService.getMapperFetcherPermissionById(id);
    }
}
