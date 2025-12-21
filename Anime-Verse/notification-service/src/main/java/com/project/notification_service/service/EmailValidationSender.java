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
            } else if (codeDto.getType().equals(EventTypeNotification.EMAIL_WELCOME.toString())) {
                helper.setSubject("Welcome to the Ultimate Anime Community!");
                helper.setText( welcomeHtml(codeDto), true);
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

    private String verificationHtml(NotificationDto dto) {
        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "<meta charset='UTF-8'/>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>"
                + "<title>Email Verification</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background:#0b0a0f;font-family:Segoe UI,Arial,sans-serif;color:#e5e7eb;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='padding:20px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background:#12060a;padding:28px;border-radius:12px;border:1px solid rgba(225,29,72,0.28);box-shadow:0 18px 48px rgba(0,0,0,0.45),0 0 32px rgba(225,29,72,0.25);'>"
                + "<tr><td align='center'>"
                + "<div style='display:inline-block;padding:6px 14px;border-radius:999px;background:rgba(225,29,72,0.14);color:#e11d48;font-size:12px;font-weight:700;letter-spacing:0.08em;text-transform:uppercase;'>ThatOtakuNetwork</div>"
                + "<h2 style='margin:12px 0 8px;font-size:22px;font-weight:800;color:#f8fafc;'>Hello, " + dto.getUsername() + "</h2>"
                + "<p style='margin:0 0 14px;color:#cbd5e1;font-size:14px;'>Use the verification code below to complete your signup/login.</p>"
                + "<div style='margin:16px 0;padding:15px 28px;background:linear-gradient(135deg,#8b1e3f,#e11d48);color:#fff;font-size:26px;font-weight:800;letter-spacing:0.14em;border-radius:10px;box-shadow:0 10px 28px rgba(225,29,72,0.35);'>" + dto.getInformation() + "</div>"
                + "<p style='margin:0 0 10px;color:#cbd5e1;font-size:13px;'>This code will expire in 5 minutes.</p>"
                + "<p style='margin:0;color:#94a3b8;font-size:12px;'>If you didn't request this, you can safely ignore this email.</p>"
                + "</td></tr></table>"
                + "</td></tr></table>"
                + "</body>"
                + "</html>";
    }

    private String welcomeHtml(NotificationDto dto) {
        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "<meta charset='UTF-8'/>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>"
                + "<title>Welcome to ThatOtakuNetwork</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background:#0b0a0f;font-family:Segoe UI,Arial,sans-serif;color:#e5e7eb;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='padding:20px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background:#12060a;padding:28px;border-radius:12px;border:1px solid rgba(225,29,72,0.28);box-shadow:0 18px 48px rgba(0,0,0,0.45),0 0 32px rgba(225,29,72,0.25);'>"
                + "<tr><td align='center'>"
                + "<div style='display:inline-block;padding:6px 14px;border-radius:999px;background:rgba(225,29,72,0.14);color:#e11d48;font-size:12px;font-weight:700;letter-spacing:0.08em;text-transform:uppercase;'>Welcome</div>"
                + "<h2 style='margin:12px 0 8px;font-size:22px;font-weight:800;color:#f8fafc;'>Welcome to ThatOtakuNetwork</h2>"
                + "<p style='margin:0 0 10px;font-size:14px;color:#cbd5e1;'>You're about to enter the vast network of otaku's.</p>"
                + "<p style='margin:0 0 12px;font-size:14px;color:#cbd5e1;'>We're thrilled to have you onboard - find new circles, swap theories, and celebrate every arc together.</p>"
                + "<p style='margin:0 0 12px;font-size:14px;color:#cbd5e1;'>From classic shounen to hidden gems, your feed is ready to ignite. Share your voice, respect the fandom, and enjoy the ride.</p>"
                + "<p style='margin:0 0 12px;font-size:14px;color:#cbd5e1;'>User: <span style='color:#e11d48;font-weight:700;'>" + dto.getUsername() + "</span></p>"
                + "<div style='margin:12px 0 16px;padding:14px;border-radius:10px;background:linear-gradient(135deg,#8b1e3f,#e11d48);color:#fff;font-size:15px;font-weight:700;box-shadow:0 10px 28px rgba(225,29,72,0.35);'>Dive in, discover new circles, and share your takes.</div>"
                + "<p style='margin:0;font-size:12px;color:#94a3b8;'>Need help? Reply to this email and we'll assist you.</p>"
                + "</td></tr></table>"
                + "</td></tr></table>"
                + "</body>"
                + "</html>";
    }
}
