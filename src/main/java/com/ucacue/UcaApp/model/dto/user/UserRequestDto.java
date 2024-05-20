package com.ucacue.UcaApp.model.dto.user;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long id; // Opcional, depende de la operación

    private String name;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String DNI;

    private String password;

    private  boolean isEnabled;

    private boolean accountNoExpired;

    private boolean accountNoLocked;

	private boolean credentialNoExpired;

    private Date  creationDate;

    @NotEmpty(message = "rolesIds cannot be empty")
    private Set<Long> rolesIds;
}
