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
        return "Email отправлен!";
    }

    @GetMapping("/health")
    public String health() {
        return "Сервис работает!";
    }
}