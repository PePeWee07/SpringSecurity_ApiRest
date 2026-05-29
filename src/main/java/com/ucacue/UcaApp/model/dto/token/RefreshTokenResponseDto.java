package com.ucacue.UcaApp.model.dto.token;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String jti;
    private boolean revoked;
    private LocalDateTime expiresAt;
    private boolean expired;

    private Long userId;
    private String userEmail;
    private String userName;
    private String userLastName;
}
