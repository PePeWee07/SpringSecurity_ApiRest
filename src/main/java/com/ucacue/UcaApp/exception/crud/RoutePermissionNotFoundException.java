package com.ucacue.UcaApp.exception.crud;

public class RoutePermissionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RoutePermissionNotFoundException(Long id) {
        super("Route permission not found with id: " + id);
    }

    public RoutePermissionNotFoundException(String path) {
        super("Route permission not found with path: " + path);
    }
}
