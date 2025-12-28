package org.ausiankou.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ausiankou.event.UserCreatedEvent;
import org.ausiankou.event.UserDeletedEvent;
import org.ausiankou.event.UserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventKafkaPublisher {

    private final KafkaProducerService kafkaProducerService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Processing UserCreatedEvent for user: {}", event.getEmail());

        UserEvent kafkaEvent = UserEvent.builder()
                .operation("CREATE")
                .email(event.getEmail())
                .userName(event.getUserName())
                .userId(event.getUserId())
                .build();

        kafkaProducerService.sendUserEvent(kafkaEvent);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        log.info("Processing UserDeletedEvent for user: {}", event.getEmail());

        UserEvent kafkaEvent = UserEvent.builder()
                .operation("DELETE")
                .email(event.getEmail())
                .userName(event.getUserName())
                .userId(event.getUserId())
                .build();

        kafkaProducerService.sendUserEvent(kafkaEvent);
    }

    @EventListener(condition = "#event.failed")
    public void handleFailedEvent(UserCreatedEvent event) {
        log.error("Failed to create user, transaction rolled back. User email: {}", event.getEmail());
    }

    @EventListener(condition = "#event.failed")
    public void handleFailedEvent(UserDeletedEvent event) {
        log.error("Failed to delete user, transaction rolled back. User email: {}", event.getEmail());
    }
}
