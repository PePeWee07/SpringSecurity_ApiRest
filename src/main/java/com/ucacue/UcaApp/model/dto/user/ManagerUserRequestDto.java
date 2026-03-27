package com.ucacue.UcaApp.model.dto.user;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ManagerUserRequestDto implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long id; // Opcional, depende de la operación

    private String name;

    private String lastName;

    @Email(message = "Must be a well-formed email address")
    @NotBlank(message = "Email is required")
    private String email;

    private String phoneNumber;

    private String address;

    @NotBlank(message = "DNI is required")
    private String dni;

    @NotBlank(message = "Password is required")
    private String password;

    private  boolean enabled;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

	private boolean credentialsNonExpired;

    private Set<Long> rolesIds;

    private LocalDateTime accountExpiryDate;
}
