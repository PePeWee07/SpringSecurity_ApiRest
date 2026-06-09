package com.ucacue.UcaApp.model.dto.catia;

import java.util.Set;

/**
 * Payload para crear/actualizar los permisos de una tool de CatIA.
 * Espejo del ToolPermissionUpsertRequest del microservicio.
 */
public record CatiaToolPermissionUpsertRequest(
        Set<String> allowedRoles,
        Boolean enabled,
        Integer cooldownSeconds) {
}
