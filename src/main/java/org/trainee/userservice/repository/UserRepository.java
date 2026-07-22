package org.trainee.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.trainee.userservice.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    @Modifying
    @Query("UPDATE User u SET u.active = true WHERE u.id = :id")
    void activate(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE User u SET u.active = false WHERE u.id = :id")
    void deactivate(@Param("id") Integer id);

    @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
    List<User> findAllByCreatedAtAfter(@Param("date") LocalDateTime date);

    Boolean existsByEmail(String email);
}
