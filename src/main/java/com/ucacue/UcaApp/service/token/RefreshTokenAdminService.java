package com.ucacue.UcaApp.service.token;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ucacue.UcaApp.model.dto.token.RefreshTokenResponseDto;

public interface RefreshTokenAdminService {

    Page<RefreshTokenResponseDto> findPaged(
            Long userId,
            String email,
            String jti,
            Boolean revoked,
            Boolean expired,
            Pageable pageable);

    void deleteById(Long id);

    long deleteAllByUserId(Long userId);
}
