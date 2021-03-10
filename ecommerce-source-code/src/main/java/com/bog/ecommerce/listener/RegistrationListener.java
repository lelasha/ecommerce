package com.bog.ecommerce.listener;

import com.bog.ecommerce.model.event.OnRegistrationCompleteEvent;
import com.bog.ecommerce.model.User;
import com.bog.ecommerce.service.EmailService;
import com.bog.ecommerce.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private EmailService mailSender;

    @SneakyThrows
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws MessagingException {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "რეგისტრაციის დამოწმება";
        String confirmationUrl = event.getAppUrl() + "/regitrationConfirm?token=" + token;
        //String message = messages.getMessage("message.regSucc", null, event.getLocale());

        mailSender.sendSimpleMessage(
                recipientAddress,
                subject,
                confirmationUrl);

    }
}
