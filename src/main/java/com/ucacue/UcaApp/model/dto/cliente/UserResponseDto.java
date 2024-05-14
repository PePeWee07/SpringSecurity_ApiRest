package com.ucacue.UcaApp.model.dto.cliente;


import lombok.*;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;

    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    private String nombre;

    @Size(min = 1, max = 50, message = "El email debe tener entre 1 y 50 caracteres")
    private String apellido;

    @Email(message = "Debe ser un correo electrónico válido")
    @Size(min = 1, max = 50, message = "El email debe tener entre 1 y 50 caracteres")
    private String email;

    @Size(max = 15, message = "El teléfono debe tener un máximo de 15 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección debe tener un máximo de 255 caracteres")
    private String direccion;

    @NotNull(message = "La cédula es obligatoria")
    @Size(min = 1, max = 15, message = "La cédula debe tener un máximo de 10 caracteres")
    private String cedula;

    // PASSWORD NO SE DEBE DEVOLVER

    private  boolean isEnabled;

    private boolean accountNoExpired;

    private boolean accountNoLocked;
    
	private boolean credentialNoExpired;

    private Date  fechaCreacion;

    private List<String> roles;

}
