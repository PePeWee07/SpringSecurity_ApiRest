package com.ucacue.UcaApp.service.permission.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.PermissionNotFoundException;
import com.ucacue.UcaApp.model.entity.PermissionEntity;
import com.ucacue.UcaApp.repository.PermissionRepository;
import com.ucacue.UcaApp.service.permission.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService{

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    @Override
    public PermissionEntity getMapperHelpPermissionById(Long id) {
        return permissionRepository.findById(id)
             .orElseThrow(() -> new PermissionNotFoundException(id));
    }
    
}
