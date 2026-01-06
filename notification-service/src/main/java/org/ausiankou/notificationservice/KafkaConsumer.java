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

    @KafkaListener(topics = "${kafka.topic.user-events:user-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserEvent event) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📥 ПОЛУЧЕНО ИЗ KAFKA:");
        System.out.println("=".repeat(50));
        System.out.println("Тип события: " + event.getEventType());
        System.out.println("Email: " + event.getEmail());
        System.out.println("Имя: " + event.getUsername());
        System.out.println("ID пользователя: " + event.getUserId());
        System.out.println("=".repeat(50));

        log.info("Получено сообщение из Kafka: {}", event);

        if ("USER_CREATED".equals(event.getEventType())) {
            String message = String.format(
                    "Здравствуйте, %s! Ваш аккаунт на сайте был успешно создан.",
                    event.getUsername()
            );
            emailService.sendEmail(event.getEmail(), "Аккаунт создан", message);
        } else if ("USER_DELETED".equals(event.getEventType())) {
            String message = String.format(
                    "Здравствуйте, %s! Ваш аккаунт был удалён.",
                    event.getUsername()
            );
            emailService.sendEmail(event.getEmail(), "Аккаунт удален", message);
        } else {
            System.out.println("⚠️ Неизвестный тип события: " + event.getEventType());
        }
    }
}
