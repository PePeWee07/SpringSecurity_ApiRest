package com.ucacue.UcaApp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.ucacue.UcaApp.auditing.AuditingData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "DNI", length = 10, nullable = false, unique = true)
    private String DNI;

    @NotNull
    @Size(min = 1, max = 60, message = "Password must be a maximum of 60 characters")
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @NotNull
    @Column(name = "enabled")
    private boolean enabled;

    @NotNull
    @Column(name = "account_No_Expired")
    private boolean accountNoExpired;

    @NotNull
    @Column(name = "account_No_Locked")
    private boolean accountNoLocked;

    @NotNull
    @Column(name = "credential_No_Expired")
    private boolean credentialNoExpired;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", schema = "auth", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = { "user_id", "role_id" }) })
    private Set<RoleEntity> roles = new HashSet<>();

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNoExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNoLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialNoExpired;
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
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                // handle the case when principal is a string (username)
                return principal.toString();
            }
        }
        return "anonymousUser";
    }
}