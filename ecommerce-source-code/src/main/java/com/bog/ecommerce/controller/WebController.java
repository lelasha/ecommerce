package com.bog.ecommerce.controller;

import com.bog.ecommerce.model.User;
import com.bog.ecommerce.model.token.PasswordResetToken;
import com.bog.ecommerce.model.token.VerificationToken;
import com.bog.ecommerce.model.dto.UserDTO;
import com.bog.ecommerce.repository.PasswordTokenRepository;
import com.bog.ecommerce.repository.ProductRepo;
import com.bog.ecommerce.repository.UserRepo;
import com.bog.ecommerce.service.AnalyticService;
import com.bog.ecommerce.service.ProductService;
import com.bog.ecommerce.service.SecurityService;
import com.bog.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;


@Controller

public class WebController {

    @Autowired
    private UserService service;
    @Autowired
    private MessageSource messages;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private AnalyticService analyticService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;
    @Autowired
    private UserRepo userRepo;


    @GetMapping("/")
    public String homePage(Model model){
        int check = 0;
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth instanceof User) {
        System.out.println("aqvar");
            check=1;
            Optional<User> user = userRepo.findById(((User) auth).getId());
            user.ifPresent(value -> model.addAttribute("balance", analyticService.getCurrencyInGEL(value.getBalance())));
        }
        model.addAttribute("check",check);
        return "index";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(Model model){
        return "login";
    }

    @GetMapping("/success-register")
    public String successRegister(){
        return "successRegister";
    }

    @GetMapping("/success-passwordchange")
    public String successPaswordChange(){
        return "successPaswordChange";
    }

    @GetMapping("/update-password")
    public String updatePassword(@RequestParam("token") String token){
        System.out.println(token + " +_+++");
        Calendar cal = Calendar.getInstance();
        PasswordResetToken verificationToken = passwordTokenRepository.findByToken(token);
        if (verificationToken != null && (verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) >= 0) {
            return "updatePassword";
        }

        return "error";
    }

    @GetMapping("/auth/my-product")
    public String myProduct(Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("products",productService.changePrice(productRepo.findAllProductByUserId(user.getId())));
        return "privatePage";
    }


    @GetMapping("/regitrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = service.getVerificationToken(token);
        if (verificationToken == null) {
            return "redirect:/badUser?error=invalid-token";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "redirect:/badUser?error=token-expired";
        }

        user.setEnabled(true);
        service.saveRegisteredUser(user);
        return "redirect:/login";
    }

    @GetMapping("/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        String result = securityService.validatePasswordResetToken(token);
        if(result != null) {
            return "redirect:/login?message=" + result;
        } else {
            model.addAttribute("token", token);
            return "redirect:/update-password";
        }
    }
}
