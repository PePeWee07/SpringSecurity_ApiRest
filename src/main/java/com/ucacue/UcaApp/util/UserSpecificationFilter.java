package com.ucacue.UcaApp.util;

import org.springframework.data.jpa.domain.Specification;

import com.ucacue.UcaApp.model.entity.UserEntity;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecificationFilter {

    public static Specification<UserEntity> filterUsers(String name, String lastName, String email, String dni) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                // Convertir ambos valores a minúsculas para una búsqueda insensible a mayúsculas
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (lastName != null && !lastName.isEmpty()) {
                // Búsqueda insensible a mayúsculas para lastName
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
            }

            if (email != null && !email.isEmpty()) {
                // Búsqueda insensible a mayúsculas para email
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (dni != null && !dni.isEmpty()) {
                // En este caso, si quieres permitir coincidencias parciales de DNI
                predicates.add(criteriaBuilder.like(root.get("dni"), "%" + dni + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }
}
