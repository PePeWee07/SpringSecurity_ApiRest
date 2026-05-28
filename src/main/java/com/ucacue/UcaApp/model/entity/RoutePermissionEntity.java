package com.ucacue.UcaApp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.ucacue.UcaApp.service.auditing.springboot.AuditingData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "route_permissions", schema = "auth")
public class RoutePermissionEntity extends AuditingData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100, message = "The route path must be a maximum of 100 characters")
    @Column(name = "path", length = 100, unique = true, nullable = false)
    private String path;

    @Size(max = 255, message = "The description must be a maximum of 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "route_permission_roles", schema = "auth",
               joinColumns = @JoinColumn(name = "route_permission_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"),
               uniqueConstraints = @UniqueConstraint(columnNames = { "route_permission_id", "role_id" }))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<RoleEntity> roleList = new HashSet<>();
}
