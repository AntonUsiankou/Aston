package org.ausiankou.notificationservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${email.mode:console}")
    private String emailMode;

    public EmailService() {
    }
    public void sendEmail(String to, String subject, String text) {
        printToConsole(to, subject, text);
    }

    public void sendUserCreatedEmail(String email, String username) {
        String subject = "Добро пожаловать! Ваш аккаунт создан";
        String text = String.format(
                "Здравствуйте, %s! Ваш аккаунт на сайте был успешно создан.",
                username != null ? username : ""
        );
        sendEmail(email, subject, text);
    }

    public void sendUserDeletedEmail(String email, String username) {
        String subject = "Ваш аккаунт был удален";
        String text = String.format(
                "Здравствуйте, %s! Ваш аккаунт был удалён.",
                username != null ? username : ""
        );
        sendEmail(email, subject, text);
    }

    private void printToConsole(String to, String subject, String text) {
        System.out.println("\n📧 [DEV MODE] Email:");
        System.out.println("════════════════════════════════════════");
        System.out.println("Кому:      " + to);
        System.out.println("Тема:      " + subject);
        System.out.println("────────────────────────────────────────");
        System.out.println("Текст:");
        System.out.println(text);
        System.out.println("════════════════════════════════════════\n");
        log.info("📧 Email (dev mode) отправлен на: {}", to);
    }
}