package com.project.notification_service.service;

import com.project.notification_service.dto.NotificationDto;
import com.project.notification_service.enums.EventTypeNotification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailValidationSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailValidationSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(NotificationDto codeDto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(codeDto.getEmail());
            helper.setFrom(fromEmail);

            if (codeDto.getType().equals(EventTypeNotification.EMAIL_VERIFICATION.toString())) {
                helper.setSubject("Email Verification Code");
                helper.setText(verificationHtml(codeDto), true);
            } else {
                throw new RuntimeException("Unsupported notification type");
            }

            mailSender.send(message);
            log.info("Verification Email sent to {} ({})", codeDto.getUsername(), codeDto.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", codeDto.getEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String verificationHtml(NotificationDto codeDto) {
        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "<meta charset='UTF-8' />"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0' />"
                + "<title>Email Verification</title>"
                + "</head>"
                + "<body style='margin:0; padding:0; background-color:#f4f4f4; font-family: Arial, sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:20px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:30px; border-radius:10px; box-shadow:0 0 10px rgba(0,0,0,0.1);'>"
                + "<tr><td style='text-align:center;'>"
                + "<h2 style='color:#333333; margin-bottom:20px;'>Hello, " + codeDto.getUsername() + "!</h2>"
                + "<p style='font-size:16px; color:#555555; margin-bottom:30px;'>We’re excited to have you on board! Your verification code is below.</p>"
                + "<div style='display:inline-block; padding:15px 25px; font-size:24px; font-weight:bold; color:#ffffff; background-color:#2E86C1; border-radius:5px; letter-spacing:2px; margin-bottom:30px;'>"
                + codeDto.getInformation()
                + "</div>"
                + "<p style='font-size:16px; color:#555555; margin-bottom:20px;'>Please enter this code to complete your verification process.</p>"
                + "<p style='font-size:12px; color:#999999;'>If you did not request this, please ignore this email.</p>"
                + "</td></tr></table></td></tr></table></body></html>";
    }

}
