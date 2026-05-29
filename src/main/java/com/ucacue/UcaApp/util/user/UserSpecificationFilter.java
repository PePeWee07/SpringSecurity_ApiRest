package com.ucacue.UcaApp.util.user;

import org.springframework.data.jpa.domain.Specification;

import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.text.Normalizer;


public class UserSpecificationFilter {

    public static Specification<UserEntity> filterUsers(Map<String, Object> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((field, value) -> {
                if (value == null) return;

                // Filtro por roles (devuelve usuarios que tengan AL MENOS uno de los roleIds).
                // Requiere JOIN con la coleccion roles + DISTINCT para evitar duplicados.
                if ("roleIds".equals(field)) {
                    if (value instanceof Collection<?> ids && !ids.isEmpty()) {
                        Join<UserEntity, RoleEntity> rolesJoin = root.join("roles");
                        predicates.add(rolesJoin.get("id").in(ids));
                        if (query != null) {
                            query.distinct(true);
                        }
                    }
                    return;
                }

                if (value.toString().isEmpty()) return;

                String normalizedValue = removeDiacritics(value.toString().toLowerCase());
                {
                    switch (field) {
                        case "id":
                            predicates.add(criteriaBuilder.equal(root.get(field), value));
                            break;
                        case "name":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get(field))),"%" + normalizedValue + "%"));
                            break;
                        case "lastName":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get(field))),"%" + normalizedValue + "%"));
                            break;
                        case "email":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%"));
                            break;
                        case "phoneNumber":
                            predicates.add(criteriaBuilder.like(root.get(field), "%" + value.toString() + "%"));
                            break;
                        case "address":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get(field))),"%" + normalizedValue + "%"));
                            break;
                        case "dni":
                            predicates.add(criteriaBuilder.like(root.get(field), "%" + value.toString() + "%"));
                            break;
                        case "enabled":
                            predicates.add(criteriaBuilder.equal(root.get(field), value));
                            break;
                        case "accountNonExpired":
                            predicates.add(criteriaBuilder.equal(root.get(field), value));
                            break;
                        case "accountNonLocked":
                            predicates.add(criteriaBuilder.equal(root.get(field), value));
                            break;
                        case "credentialsNonExpired":
                            predicates.add(criteriaBuilder.equal(root.get(field), value));
                            break;
                        case "accountExpiryDate":
                            predicates.add(criteriaBuilder.equal(root.get(field), value));
                            break;
                        // case "createdBy":
                        //     predicates.add(criteriaBuilder.like(
                        //             criteriaBuilder.lower(root.get("createdBy")),
                        //             "%" + value.toString().toLowerCase() + "%"                     ));
                        //     break;
                        default:
                            break;
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String removeDiacritics(String input) {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = input.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return input;
    }
}    
