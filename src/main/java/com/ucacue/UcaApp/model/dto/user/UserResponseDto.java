package com.ucacue.UcaApp.model.dto.user;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String DNI;

    // PASSWORD NO SE DEBE DEVOLVER

    private  boolean isEnabled;

    private boolean accountNoExpired;

    private boolean accountNoLocked;
    
	private boolean credentialNoExpired;

    private Date  creationDate;

    private List<RoleResponseDto> roles;

}
