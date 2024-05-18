package com.ucacue.UcaApp.model.dto.role;

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
public class RoleResponseDto {
    private Long id;
    private String name;
    private List<PermissionResponseDto> permissionList;
}