package com.ucacue.UcaApp.exception.crud;

public class PermissionNotFoundException extends RuntimeException {
    private Long permissionId;

    public PermissionNotFoundException(Long permissionId) {
        super("Permission not found with ID: " + permissionId);
        this.permissionId = permissionId;
    }

    public Long getPermissionId() {
        return permissionId;
    }
}
