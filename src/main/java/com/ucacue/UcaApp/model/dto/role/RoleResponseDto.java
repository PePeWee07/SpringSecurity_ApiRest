package com.ucacue.UcaApp.model.dto.role;

import java.io.Serializable;
import java.util.List;

import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;
    
    private List<PermissionResponseDto> permissionList;
}