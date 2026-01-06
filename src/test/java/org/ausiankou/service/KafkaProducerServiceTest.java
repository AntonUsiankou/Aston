package org.ausiankou.service;

import org.ausiankou.dto.events.UserKafkaEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class KafkaProducerServiceTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testSendUserCreatedEvent() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        String username = "Test User";

        // When
        kafkaProducerService.sendUserCreatedEvent(userId, email, username);

        // Then
        verify(kafkaTemplate, times(1)).send(
                eq("user-events"),
                argThat(event -> {
                    UserKafkaEvent kafkaEvent = (UserKafkaEvent) event;
                    return "USER_CREATED".equals(kafkaEvent.getEventType()) &&
                            email.equals(kafkaEvent.getEmail()) &&
                            username.equals(kafkaEvent.getUsername()) &&
                            userId.equals(kafkaEvent.getUserId());
                })
        );
    }

    @Test
    void testSendUserDeletedEvent() {
        // Given
        Long userId = 2L;
        String email = "delete@example.com";
        String username = "Delete User";

        // When
        kafkaProducerService.sendUserDeletedEvent(userId, email, username);

        // Then
        verify(kafkaTemplate, times(1)).send(
                eq("user-events"),
                argThat(event -> {
                    UserKafkaEvent kafkaEvent = (UserKafkaEvent) event;
                    return "USER_DELETED".equals(kafkaEvent.getEventType()) &&
                            email.equals(kafkaEvent.getEmail()) &&
                            username.equals(kafkaEvent.getUsername()) &&
                            userId.equals(kafkaEvent.getUserId());
                })
        );
    }
}
