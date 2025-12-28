package org.ausiankou.service;

import lombok.extern.slf4j.Slf4j;
import org.ausiankou.config.KafkaProducerService;
import org.ausiankou.event.UserEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false", matchIfMissing = true)
public class KafkaProducerServiceStub extends KafkaProducerService {

    public KafkaProducerServiceStub(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void sendUserEvent(UserEvent event) {
        log.info("[STUB] Событие отправлено в Kafka (заглушка): {}", event);
        System.out.println("\n📤 [STUB] Kafka событие:");
        System.out.println("   Операция: " + event.getOperation());
        System.out.println("   Email: " + event.getEmail());
        System.out.println("   Имя: " + event.getUserName());
        System.out.println("   ID: " + event.getUserId());
    }
}
