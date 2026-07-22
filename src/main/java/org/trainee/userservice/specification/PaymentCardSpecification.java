package org.trainee.userservice.specification;

import org.springframework.data.jpa.domain.Specification;
import org.trainee.userservice.model.PaymentCard;
import org.trainee.userservice.specification.filter.PaymentCardFilter;

public class PaymentCardSpecification {
    private PaymentCardSpecification() {}

    public static Specification<PaymentCard> holder(String holder) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("holder")), "%" + holder.toLowerCase() + "%")
        );
    }

    public static Specification<PaymentCard> byFilter(PaymentCardFilter filter) {
        return Specification
                .where(holder(filter.getHolder()));
    }
}
