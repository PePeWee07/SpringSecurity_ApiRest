package com.ucacue.UcaApp.model.dto.cliente;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
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

    private Set<Long> rolesIds;


}
