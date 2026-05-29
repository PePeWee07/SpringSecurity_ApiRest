package com.ucacue.UcaApp.service.token.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucacue.UcaApp.exception.crud.ResourceNotFound;
import com.ucacue.UcaApp.model.dto.token.RefreshTokenResponseDto;
import com.ucacue.UcaApp.model.entity.RefreshTokenEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.repository.RefreshTokenRepository;
import com.ucacue.UcaApp.service.token.RefreshTokenAdminService;
import com.ucacue.UcaApp.util.token.RefreshTokenSpecificationFilter;

@Service
public class RefreshTokenAdminServiceImpl implements RefreshTokenAdminService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<RefreshTokenResponseDto> findPaged(
            Long userId,
            String email,
            String jti,
            Boolean revoked,
            Boolean expired,
            Pageable pageable) {

        Specification<RefreshTokenEntity> spec =
                RefreshTokenSpecificationFilter.build(userId, email, jti, revoked, expired);

        Page<RefreshTokenEntity> page = refreshTokenRepository.findAll(spec, pageable);
        LocalDateTime now = LocalDateTime.now();

        return page.map(token -> toDto(token, now));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!refreshTokenRepository.existsById(id)) {
            throw new ResourceNotFound("Refresh token not found with id: " + id);
        }
        refreshTokenRepository.deleteById(id);
    }

    @Override
    @Transactional
    public long deleteAllByUserId(Long userId) {
        return refreshTokenRepository.deleteAllByUserId(userId);
    }

    private RefreshTokenResponseDto toDto(RefreshTokenEntity token, LocalDateTime now) {
        UserEntity user = token.getUser();
        return RefreshTokenResponseDto.builder()
                .id(token.getId())
                .jti(token.getJti())
                .revoked(token.isRevoked())
                .expiresAt(token.getExpiresAt())
                .expired(token.getExpiresAt() != null && token.getExpiresAt().isBefore(now))
                .userId(user != null ? user.getId() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .userName(user != null ? user.getName() : null)
                .userLastName(user != null ? user.getLastName() : null)
                .build();
    }
}
