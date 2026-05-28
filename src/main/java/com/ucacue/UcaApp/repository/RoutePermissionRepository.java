package com.ucacue.UcaApp.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucacue.UcaApp.model.entity.RoutePermissionEntity;

@Repository
public interface RoutePermissionRepository extends JpaRepository<RoutePermissionEntity, Long> {
    Optional<RoutePermissionEntity> findByPath(String path);
    List<RoutePermissionEntity> findByPathIn(List<String> paths);
    boolean existsByPath(String path);
}
