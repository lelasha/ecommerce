package com.bog.ecommerce.repository;

import com.bog.ecommerce.model.token.PasswordResetToken;
import com.bog.ecommerce.model.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordTokenRepository  extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);
    @Query(value = "select * from password_reset_token where user_id = ?1",nativeQuery = true)
    Optional<PasswordResetToken> findByUserId(Long userid);
}
