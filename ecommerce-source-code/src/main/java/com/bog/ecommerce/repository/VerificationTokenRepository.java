package com.bog.ecommerce.repository;

import com.bog.ecommerce.model.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {

    VerificationToken findByToken(String token);

    @Query(value = "select * from verification_token where user_id = ?1",nativeQuery = true)
    Optional<VerificationToken> findByUserId(Long userId);
}
