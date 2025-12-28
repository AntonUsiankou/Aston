package org.ausiankou.notificationservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topic.user-events:user-events-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserEvent event) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📥 ПОЛУЧЕНО ИЗ KAFKA:");
        System.out.println("=".repeat(50));
        System.out.println("Операция: " + event.getOperation());
        System.out.println("Email: " + event.getEmail());
        System.out.println("Имя: " + event.getUserName());
        System.out.println("=".repeat(50));

        log.info("Получено сообщение из Kafka: {}", event);

        if ("CREATE".equals(event.getOperation())) {
            String message = String.format(
                    "Здравствуйте, %s! Ваш аккаунт на сайте был успешно создан.",
                    event.getUserName()
            );
            emailService.sendEmail(event.getEmail(), "Аккаунт создан", message);
        } else if ("DELETE".equals(event.getOperation())) {
            String message = String.format(
                    "Здравствуйте, %s! Ваш аккаунт был удалён.",
                    event.getUserName()
            );
            emailService.sendEmail(event.getEmail(), "Аккаунт удален", message);
        }
    }
}
