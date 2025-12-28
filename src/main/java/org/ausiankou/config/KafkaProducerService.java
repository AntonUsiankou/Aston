package org.ausiankou.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.ausiankou.event.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${kafka.topic.user-events:user-events-topic}")
    private String userEventsTopic;

    public void sendUserEvent(UserEvent event) {
        log.info("Отправка события в Kafka: {}", event);

        try {
            kafkaTemplate.send(userEventsTopic, event);
            log.info("Событие отправлено в топик: {}", userEventsTopic);
        } catch (Exception e) {
            log.error("Ошибка отправки в Kafka: {}", e.getMessage());
        }
    }
}
