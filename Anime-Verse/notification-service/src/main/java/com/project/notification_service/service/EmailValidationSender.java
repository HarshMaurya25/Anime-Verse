package com.project.notification_service.service;

import com.project.notification_service.dto.NotificationDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.MalformedInputException;

import static java.rmi.server.LogStream.log;

@Slf4j
@Service
public class EmailValidationSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailValidationSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(NotificationDto codeDto){
        try {
            MimeMessage message = mailSender.createMimeMessage();

            String toEmail = codeDto.getEmail();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your Verification Code");

            String htmlContent = "<html><body>"
                    + "<h2>Hello, " + codeDto.getUsername() + "!</h2>"
                    + "<p>Your verification code is:</p>"
                    + "<p style='font-size:24px; font-weight:bold; color:#2E86C1;'>" + codeDto.getInformation() + "</p>"
                    + "<p>Please use this code to complete your verification.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification Email is Send {} on {}" , codeDto.getUsername() ,codeDto.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
