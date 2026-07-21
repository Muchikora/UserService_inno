package org.trainee.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards", indexes = {
        @Index(name = "idx_payment_card_number", columnList = "number"),
        @Index(name = "idx_payment_card_holder", columnList = "holder"),
})
@Getter
@Setter
public class PaymentCard extends BaseEntity {
    @Column(name = "number", length = 16, nullable = false)
    private String number;

    @Column(name = "holder", length = 64, nullable = false)
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
