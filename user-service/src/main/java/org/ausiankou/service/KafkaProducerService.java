package org.ausiankou.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ausiankou.dto.events.UserKafkaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "user-events";

    public void sendUserCreatedEvent(Long userId, String email, String username){
        UserKafkaEvent event = new UserKafkaEvent("USER_CREATED", email, username, userId);
        sendEvent(event);
        log.info("Отправлено событие USER_CREATED для пользователя: {}", email);
    }
    public void sendUserDeletedEvent(Long userId, String email, String username){
        UserKafkaEvent event = new UserKafkaEvent("USER_DELETE", email, username, userId);
        sendEvent(event);
        log.info("Отправлено событие USER_DELETED для пользователя: {}", email);
    }

    public void sendEvent(UserKafkaEvent event){
        try{
            kafkaTemplate.send(TOPIC, event);
            log.info("Событие отправлено в Kafka. Тип: {}, Пользователь: {}",
                    event.getEventType(), event.getEmail());
        }catch (Exception ex){
            log.error("Ошибка при отправке события в Kafka: {}", ex.getMessage());
            throw new RuntimeException("Failed to send event to Kafka", ex);
        }
    }
}
