package com.ucacue.UcaApp.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucacue.UcaApp.model.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>{
    RoleEntity findByName(String name);
    List<RoleEntity> findByNameIn(List<String> roleNames);

    List<RoleEntity> findByPermissionListId(Long permissionId);
} 
