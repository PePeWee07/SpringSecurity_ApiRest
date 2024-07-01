package com.ucacue.UcaApp.model.dto.user;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long id;

    private String name;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String DNI;

    private String password;
}
