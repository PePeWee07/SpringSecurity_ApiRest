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
    @Size(min = 1, max = 50, message = "The name must have a maximum of 50 characters")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(min = 1, max = 50, message = "The last name must have a maximum of 50 characters")
    @Column(name = "lastName", length = 50, nullable = false)
    private String lastName;

    @NotNull
    @Email(message = "Must be a valid email")
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(min = 1, max = 10, message = "The phone number must have a maximum of 10 characters")
    @Column(name = "phoneNumber", length = 10)
    private String phoneNumber;

    @NotNull
    @Size(min = 1, max = 255, message = "The address must be a maximum of 255 characters")
    @Column(name = "address", length = 255)
    private String address;

    @NotNull(message = "The DNI is mandatory")
    @Size(min = 1, max = 10, message = "The DNI must have a maximum of 10 characters")
    @Column(name = "DNI", length = 10, nullable = false, unique = true)
    private String DNI;

    @NotNull
    @Size(min = 1, max = 150, message = "Password must be a maximum of 150 characters")
    @Column(name = "password", length = 150, nullable = false)
    private String password;

    @NotNull
    @Column(name = "is_enabled")
    private boolean isEnabled;

    @NotNull
    @Column(name = "account_No_Expired")
    private boolean accountNoExpired;

    @NotNull
    @Column(name = "account_No_Locked")
    private boolean accountNoLocked;

    @NotNull
    @Column(name = "credential_No_Expired")
    private boolean credentialNoExpired;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Column(name = "creationDate", nullable = false)
    private Date  creationDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"),
    uniqueConstraints= {@UniqueConstraint(columnNames= {"user_id", "role_id"})})
    private Set<RolesEntity> roles = new HashSet<>();
}