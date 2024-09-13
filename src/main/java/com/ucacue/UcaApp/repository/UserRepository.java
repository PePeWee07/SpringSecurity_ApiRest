package com.ucacue.UcaApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ucacue.UcaApp.model.entity.UserEntity;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    List<UserEntity> findByRolesId(Long roleId);
}
