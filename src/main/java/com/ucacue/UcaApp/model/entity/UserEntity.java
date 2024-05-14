package com.ucacue.UcaApp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @NotNull
    @Size(min = 1, max = 50, message = "El apellido debe tener entre 1 y 50 caracteres")
    @Column(name = "apellido", length = 50, nullable = false)
    private String apellido;

    @NotNull
    @Email(message = "Debe ser un correo electrónico válido")
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(min = 1, max = 15, message = "El teléfono debe tener un máximo de 15 caracteres")
    @Column(name = "telefono", length = 15)
    private String telefono;

    @NotNull
    @Size(min = 1, max = 255, message = "La dirección debe tener un máximo de 255 caracteres")
    @Column(name = "direccion", length = 255)
    private String direccion;

    @NotNull(message = "La cédula es obligatoria")
    @Size(min = 1, max = 10, message = "La cédula debe tener un máximo de 10 caracteres")
    @Column(name = "cedula", length = 10, nullable = false, unique = true)
    private String cedula;

    private String password;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_No_Expired")
    private boolean accountNoExpired;

    @Column(name = "account_No_Locked")
    private boolean accountNoLocked;

    @Column(name = "credential_No_Expired")
    private boolean credentialNoExpired;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(name = "fechaCreacion", nullable = false)
    private Date  fechaCreacion;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"),
    uniqueConstraints= {@UniqueConstraint(columnNames= {"user_id", "role_id"})})
    private Set<RolesEntity> roles = new HashSet<>();
}