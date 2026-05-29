package com.ucacue.UcaApp.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ucacue.UcaApp.model.entity.RefreshTokenEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;

import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long>, JpaSpecificationExecutor<RefreshTokenEntity> {

    // Buscar token activo por jti
    Optional<RefreshTokenEntity> findByJtiAndRevokedFalse(String jti);

    // Buscar tokens activos por email del usuario y fecha de expiración
    long countByUserEmailAndRevokedFalseAndExpiresAtAfter(String email, LocalDateTime now);

    // Limpiar tokens expirados
    void deleteByExpiresAtBefore(LocalDateTime now);

    // Eliminar todos los tokens de un usuario específico
    @Transactional
    void deleteAllByUser(UserEntity user);

    // Eliminar todos los tokens de un usuario (por id)
    @Transactional
    long deleteAllByUserId(Long userId);

}