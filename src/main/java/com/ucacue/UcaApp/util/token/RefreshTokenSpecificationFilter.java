package com.ucacue.UcaApp.util.token;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.ucacue.UcaApp.model.entity.RefreshTokenEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

/**
 * Construye una Specification dinamica sobre RefreshTokenEntity para el panel
 * de administracion. Todos los filtros son opcionales y se combinan con AND.
 */
public class RefreshTokenSpecificationFilter {

    public static Specification<RefreshTokenEntity> build(
            Long userId,
            String email,
            String jti,
            Boolean revoked,
            Boolean expired) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtros que tocan el usuario relacionado: hacen JOIN una sola vez
            if ((userId != null) || (email != null && !email.isBlank())) {
                Join<RefreshTokenEntity, UserEntity> userJoin = root.join("user");
                if (userId != null) {
                    predicates.add(cb.equal(userJoin.get("id"), userId));
                }
                if (email != null && !email.isBlank()) {
                    predicates.add(cb.like(cb.lower(userJoin.get("email")), "%" + email.toLowerCase() + "%"));
                }
            }

            if (jti != null && !jti.isBlank()) {
                predicates.add(cb.like(root.get("jti"), "%" + jti + "%"));
            }

            if (revoked != null) {
                predicates.add(cb.equal(root.get("revoked"), revoked));
            }

            if (expired != null) {
                LocalDateTime now = LocalDateTime.now();
                if (expired) {
                    predicates.add(cb.lessThan(root.get("expiresAt"), now));
                } else {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("expiresAt"), now));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
