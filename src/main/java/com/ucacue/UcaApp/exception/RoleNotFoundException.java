package com.ucacue.UcaApp.exception;

public class RoleNotFoundException extends RuntimeException {
    private Long roleId;

    public RoleNotFoundException(Long roleId) {
        super("Role not found with ID: " + roleId);
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }
}
