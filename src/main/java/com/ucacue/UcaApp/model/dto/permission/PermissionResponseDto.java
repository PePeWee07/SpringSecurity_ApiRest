package com.ucacue.UcaApp.model.dto.permission;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private String name;
}
