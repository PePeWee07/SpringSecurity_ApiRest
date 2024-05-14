package com.ucacue.UcaApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucacue.UcaApp.model.entity.RolesEntity;

@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Long>{
    RolesEntity findByName(String name);
} 
