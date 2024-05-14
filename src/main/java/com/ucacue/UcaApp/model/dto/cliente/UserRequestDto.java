package com.ucacue.UcaApp.model.dto.cliente;

import lombok.*;

import java.util.Date;
import java.util.Set;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private Long id; // Opcional, depende de la operación

    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    private String nombre;

    @Size(min = 1, max = 50, message = "El apellido debe tener entre 1 y 50 caracteres")
    private String apellido;

    @Email(message = "Debe ser un correo electrónico válido")
    @Size(min = 1, max = 50, message = "El email debe tener entre 1 y 50 caracteres")
    private String email;

    @Size(max = 15, message = "El teléfono debe tener un máximo de 15 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección debe tener un máximo de 255 caracteres")
    private String direccion;

    @Size(min = 1, max = 15, message = "La cédula debe tener un máximo de 10 caracteres")
    private String cedula;

    @Size(min = 1, max = 150, message = "La contraseña debe tener entre 1 y 150 caracteres")
    private String password;

    private  boolean isEnabled;

    private boolean accountNoExpired;

    private boolean accountNoLocked;

	private boolean credentialNoExpired;

    private Date  fechaCreacion;

    private Set<Long> rolesIds;


}
