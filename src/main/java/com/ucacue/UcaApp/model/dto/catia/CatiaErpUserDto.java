package com.ucacue.UcaApp.model.dto.catia;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public record CatiaErpUserDto(
        String codigoErp,
        String tipoIdentificacion,
        String identificacion,
        String nombres,
        String apellidos,
        String numeroCelular,
        String emailInstitucional,
        String emailPersonal,
        String sexo,
        List<JsonNode> rolesUsuario) {
}
