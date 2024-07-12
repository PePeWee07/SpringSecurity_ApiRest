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
@Table(name = "roles", schema = "auth")
public class RoleEntity extends AuditingData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50, message = "The Role name must be a maximum of 50 characters")
    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "roles_permissions", schema = "auth", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = { "role_id", "permission_id" }) })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<PermissionEntity> permissionList = new HashSet<>();
}
