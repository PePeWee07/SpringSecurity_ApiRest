package com.ucacue.UcaApp.util;

import org.springframework.data.jpa.domain.Specification;

import com.ucacue.UcaApp.model.entity.UserEntity;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserSpecificationFilter {

    public static Specification<UserEntity> filterUsers(Map<String, Object> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((field, value) -> {
                if (value != null && !value.toString().isEmpty()) {
                    switch (field) {
                        case "name":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%"));
                            break;
                        case "lastName":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%"));
                            break;
                        case "email":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%"));
                            break;
                        case "phoneNumber":
                            predicates.add(criteriaBuilder.like(root.get(field), "%" + value.toString() + "%"));
                            break;
                        case "address":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%"));
                            break;
                        case "dni":
                            predicates.add(criteriaBuilder.like(root.get(field), "%" + value.toString() + "%"));
                            break;
                        // case "enabled":
                        //     predicates.add(criteriaBuilder.equal(root.get(field), value));
                        //     break;
                        // case "account_non_expired":
                        //     predicates.add(criteriaBuilder.equal(root.get(field), value));
                        //     break;
                        // case "account_non_locked":
                        //     predicates.add(criteriaBuilder.equal(root.get(field), value));
                        //     break;
                        // case "credentials_non_expired":
                        //     predicates.add(criteriaBuilder.equal(root.get(field), value));
                        //     break;
                        // case "roles":
                        //     predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%"));
                        //     break;
                        // case "accountExpiryDate":
                        //     predicates.add(createDateEqualPredicate(root, criteriaBuilder, field, value));
                        //     break;
                        // agregar más campos
                        default:
                            // Ignorar otros campos o hacer algo por defecto
                            break;
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}    
