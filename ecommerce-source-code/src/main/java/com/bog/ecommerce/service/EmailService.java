package com.bog.ecommerce.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;



    public void sendSimpleMessage(
            String to, String subject, String text, File file) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setPriority(1);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        helper.addAttachment(file.getName(),file);

        emailSender.send(message);
    }
    public void sendSimpleMessage(
            String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setPriority(1);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        emailSender.send(message);
    }
}
