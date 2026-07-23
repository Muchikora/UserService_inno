package org.trainee.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.trainee.userservice.model.PaymentCard;

import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {
    List<PaymentCard> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE PaymentCard u SET u.active = true WHERE u.id = :id")
    void activate(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PaymentCard u SET u.active = false WHERE u.id = :id")
    void deactivate(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM PaymentCard c WHERE c.user.id = :userId")
    long countByUserId(Long userId);
}
