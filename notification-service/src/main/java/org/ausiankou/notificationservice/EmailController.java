package org.ausiankou.notificationservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam String to,
                            @RequestParam String subject,
                            @RequestParam String text) {
        emailService.sendEmail(to, subject, text);
        return "✅ Email отправлен на адрес: " + to;
    }

    @PostMapping("/welcome")
    public String sendWelcomeEmail(@RequestParam String email,
                                   @RequestParam(required = false, defaultValue = "Пользователь") String name) {
        emailService.sendUserCreatedEmail(email, name);
        return "✅ Приветственное письмо отправлено на: " + email;
    }

    @PostMapping("/goodbye")
    public String sendGoodbyeEmail(@RequestParam String email,
                                   @RequestParam(required = false, defaultValue = "Пользователь") String name) {
        emailService.sendUserDeletedEmail(email, name);
        return "✅ Письмо об удалении отправлено на: " + email;
    }

    @GetMapping("/health")
    public String health() {
        return "✅ Сервис уведомлений работает!";
    }
}