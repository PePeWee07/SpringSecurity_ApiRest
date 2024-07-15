package com.ucacue.UcaApp.model.dto.user;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserManagerRequestDto implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long id; // Opcional, depende de la operación

    private String name;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String DNI;

    private String password;

    private  boolean enabled;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

	private boolean credentialsNonExpired;

    private Set<Long> rolesIds;

    private Date accountExpiryDate;
}
