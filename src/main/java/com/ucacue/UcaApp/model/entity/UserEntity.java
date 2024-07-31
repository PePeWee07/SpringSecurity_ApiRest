package com.ucacue.UcaApp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ucacue.UcaApp.service.auditing.springboot.AuditingData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "auth")
public class UserEntity extends AuditingData implements UserDetails {

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
    @Column(name = "dni", length = 10, nullable = false, unique = true)
    private String dni;

    @NotNull
    @Size(min = 1, max = 60, message = "Password must be a maximum of 60 characters")
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @NotNull
    @Column(name = "enabled")
    private boolean enabled;

    @NotNull
    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @NotNull
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @NotNull
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", schema = "auth", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = { "user_id", "role_id" }) })
    private Set<RoleEntity> roles = new HashSet<>();

    @Column(name = "account_expiry_date")
    private Date accountExpiryDate;

    @Override
    public boolean isAccountNonExpired() {
        if (this.accountExpiryDate != null) {
            System.out.println("accountExpiryDate: " + new Date().before(this.accountExpiryDate));
            return new Date().before(this.accountExpiryDate);
        }
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .flatMap(role -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                    role.getPermissionList()
                            .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
                    return authorities.stream();
                })
                .collect(Collectors.toSet());
    }

    @Override
    @JsonProperty("username")
    public String getUsername() {
        return this.email;
    }
}