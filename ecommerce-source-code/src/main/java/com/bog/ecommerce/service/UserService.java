package com.bog.ecommerce.service;

import com.bog.ecommerce.exception.CustomException;
import com.bog.ecommerce.model.User;
import com.bog.ecommerce.model.token.PasswordResetToken;
import com.bog.ecommerce.model.token.VerificationToken;
import com.bog.ecommerce.model.dto.UserDTO;
import com.bog.ecommerce.repository.PasswordTokenRepository;
import com.bog.ecommerce.repository.UserRepo;
import com.bog.ecommerce.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    public User userRegistration(UserDTO userDTO) throws CustomException {
        Optional<User> userCheck = userRepo.findByEmail(userDTO.getEmail());
        if (userCheck.isEmpty()) {
            User user = new User();
            user.setEmail(userDTO.getEmail());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setPersonalId(userDTO.getPersonalId());
            user.setIBAN(userDTO.getIBAN());

            return userRepo.save(user);
        }
        throw new CustomException("ასეთი მეილი უკვე არსებობს " + userDTO.getEmail());
    }




    public User getUser(String verificationToken) {
        return tokenRepository.findByToken(verificationToken).getUser();
    }


    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }


    public void saveRegisteredUser(User user) {
        userRepo.save(user);
    }


    public void createVerificationToken(User user, String token) {
        Optional<VerificationToken> checkToken = tokenRepository.findByUserId(user.getId());
        if (checkToken.isPresent()) {
            tokenRepository.deleteById(checkToken.get().getId());
        }
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        Optional<PasswordResetToken> checkToken = passwordTokenRepository.findByUserId(user.getId());
        if (checkToken.isPresent()) {
            passwordTokenRepository.deleteById(checkToken.get().getId());
        }
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public User getUserByPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token).getUser();
    }

    public void changeUserPassword(User user, String password) {

        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
        tokenRepository.findByUserId(user.getId()).
                ifPresent(verificationToken -> tokenRepository.deleteById(verificationToken.getId()));
    }
}
