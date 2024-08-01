package com.ucacue.UcaApp.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    // Constructor que acepta token y email
    public RevokedToken(String token, String email) {
        this.token = token;
        this.email = email;
        this.revokedAt = LocalDateTime.now();
    }
}
