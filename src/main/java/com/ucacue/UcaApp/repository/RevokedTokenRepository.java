package com.ucacue.UcaApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucacue.UcaApp.model.entity.RevokedTokenEntity;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, Long> {
    boolean existsByToken(String token);
}
