package org.ausiankou.notificationservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.mode:console}")
    private String emailMode;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        if ("console".equals(emailMode)) {
            System.out.println("\n📧 [DEV] Email would be sent:");
            System.out.println("   To: " + to);
            System.out.println("   Subject: " + subject);
            System.out.println("   Text: " + text);
        } else {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            try {
                mailSender.send(message);
                log.info("Email sent to: {}", to);
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage());
            }
        }
    }
}