package com.bog.ecommerce.service;

import com.bog.ecommerce.model.User;
import com.bog.ecommerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Optional<User> user = userRepo.findByEmail(username);
        if (user.isPresent()) {
            User user1= user.get();
            if (!user1.isEnabled()){
                throw new UsernameNotFoundException(
                        "გთხოვთ გააქტიუროთ იუზერი მეილზე მოსული შეტყობინებით");
            }
            return user1;
        }
        throw new UsernameNotFoundException(
                "მაილი '" + username + "' ვერ იქნა ნაპოვნი");

    }
}
