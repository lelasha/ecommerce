package com.bog.ecommerce.controller;

import com.bog.ecommerce.exception.CustomException;
import com.bog.ecommerce.model.dto.PasswordDto;
import com.bog.ecommerce.model.event.OnRegistrationCompleteEvent;
import com.bog.ecommerce.model.User;
import com.bog.ecommerce.model.dto.UserDTO;
import com.bog.ecommerce.model.response.ApiError;
import com.bog.ecommerce.repository.UserRepo;
import com.bog.ecommerce.service.EmailService;
import com.bog.ecommerce.service.SecurityService;
import com.bog.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Optional;
import java.util.UUID;

@Controller
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecurityService securityService;


    @PostMapping("/register")
    public ModelAndView registerUserAccount(@ModelAttribute("userDTO") @Valid final UserDTO userDTO, final BindingResult bindingResult,final HttpServletRequest request, final Errors errors) {
        if (bindingResult.hasErrors() || errors.hasErrors()) {
            //mav.addObject("message",bindingResult.getAllErrors());
            return new ModelAndView("register", "user", userDTO);
        }
        try {
            final User registered = userService.userRegistration(userDTO);

            final String appUrl = "http://www." + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        } catch (final CustomException e) {
            ModelAndView mav = new ModelAndView("register", "user", userDTO);
            mav.addObject("message", e.getMessage());
            return mav;
        }
        return new ModelAndView("successRegister", "user", userDTO);
    }

    @PostMapping("/resetPassword")
    public ModelAndView resetPassword(HttpServletRequest request, @RequestParam("email") @Email(message = "გთხოვთ მიუთითოთ სწორი მაილი")  String userEmail) throws MessagingException {
        Optional<User> user = userRepo.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("ასეთი იუზერი ვერ მოიძებნა");
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user.get(), token);

        String url = request.getRequestURL().toString()
                .split("resetPassword")[0] + "changePassword?token=" + token;
        emailService.sendSimpleMessage(user.get().getEmail(), "პაროლის აღდგენა",url);

        return new ModelAndView("successPaswordChange","user",user.get());
    }

    @PostMapping("/savePassword")
    public ModelAndView savePassword(@Valid PasswordDto passwordDto) {
        String result = securityService.validatePasswordResetToken(passwordDto.getToken());
        if(result != null) {
            return new ModelAndView("error","error",result);

        }
        User user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user != null) {
            userService.changeUserPassword(user, passwordDto.getNewPassword());
            return new ModelAndView("successPaswordChange","user",user);

        } else {
            return new ModelAndView("error","error","ასეთი იუზერი ვერ მოიძებნა");

        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> onValidationError(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage().trim().split(":")[1]));
    }

}
