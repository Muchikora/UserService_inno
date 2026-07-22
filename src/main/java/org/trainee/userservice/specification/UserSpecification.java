package org.trainee.userservice.specification;

import org.springframework.data.jpa.domain.Specification;
import org.trainee.userservice.model.User;
import org.trainee.userservice.specification.filter.UserFilter;

public class UserSpecification {
    private UserSpecification() {}

    public static Specification<User> name(String name) {
        return ((root, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%")
        );
    }

    public static Specification<User> surname(String surname) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + surname.toLowerCase() + "%")
        );
    }

    public static Specification<User> byFilter(UserFilter filter) {
        return Specification
                .where(name(filter.getName()))
                .and(surname(filter.getSurname()));
    }
}
