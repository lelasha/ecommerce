package com.bog.ecommerce.repository;

import com.bog.ecommerce.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserAuthRepo extends JpaRepository<UserAuth, Long> {

    /**
     * მოცემულ დღეს უნიკალური მომხმარებლების რაოდენობა, რომელთაც გაიარეს ავტორიზაცია
     */
    @Query(value = "select count(distinct user_id) from user_auth where created_at = ?1", nativeQuery = true)
    Optional<Integer> totalDistinctUserAuth(Date date);


}
