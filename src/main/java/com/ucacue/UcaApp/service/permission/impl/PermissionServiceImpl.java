package com.ucacue.UcaApp.service.permission.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.crud.PermissionNotFoundException;
import com.ucacue.UcaApp.model.dto.permission.PermissionRequestDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.model.entity.PermissionEntity;
import com.ucacue.UcaApp.model.mapper.PermissionMapper;
import com.ucacue.UcaApp.repository.PermissionRepository;
import com.ucacue.UcaApp.service.permission.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    @Override
    public PermissionEntity getMapperFetcherPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponseDto> findAll() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::permissionEntityToPermissionResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public PermissionResponseDto getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::permissionEntityToPermissionResponseDto)
                .orElseThrow(() -> new PermissionNotFoundException(id));
    }

    @Transactional
    @Override
    public PermissionResponseDto save(PermissionRequestDto dto) {
        PermissionEntity entity = permissionMapper.permissionRequestDtoToPermissionEntity(dto);
        PermissionEntity savedEntity = permissionRepository.save(entity);
        return permissionMapper.permissionEntityToPermissionResponseDto(savedEntity);
    }

    @Transactional
    @Override
    public PermissionResponseDto update(Long id, PermissionRequestDto dto) {
        PermissionEntity entity = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));
        permissionMapper.updateEntityFromDto(dto, entity);
        PermissionEntity updatedEntity = permissionRepository.save(entity);
        return permissionMapper.permissionEntityToPermissionResponseDto(updatedEntity);
    }

    @Override
    public boolean exists(Long id) {
        return permissionRepository.existsById(id);
    }

    @Override
    public void deletePermissionById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePermissionById'");
    }
}
