package com.bog.ecommerce.service;

import com.bog.ecommerce.model.token.PasswordResetToken;
import com.bog.ecommerce.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class SecurityService {

    @Autowired
    PasswordTokenRepository passwordTokenRepository;


    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "არასწორი ლინკი, გთხოვთ სცადოთ ახლიდან"
                : isTokenExpired(passToken) ? "ლინკს გასული აქვს ვადა, გთხოვთ სცადოთ ახლიდან"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
