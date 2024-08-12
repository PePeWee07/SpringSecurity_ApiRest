package com.ucacue.UcaApp.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucacue.UcaApp.model.entity.RevokedTokenEntity;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, Long> {
    boolean existsByToken(String token);
    Optional<RevokedTokenEntity> findByEmail(String email);
    List<RevokedTokenEntity> findAllByRevokedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
