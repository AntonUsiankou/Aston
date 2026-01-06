package org.ausiankou;

import org.ausiankou.dto.events.UserKafkaEvent;
import org.ausiankou.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Captor
    private ArgumentCaptor<UserKafkaEvent> eventCaptor;

    @Test
    void sendUserCreatedEvent_shouldSendCorrectEvent() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        String username = "Test User";

        // When
        kafkaProducerService.sendUserCreatedEvent(userId, email, username);

        // Then
        verify(kafkaTemplate).send(eq("user-events"), eventCaptor.capture());

        UserKafkaEvent capturedEvent = eventCaptor.getValue();
        assertEquals("USER_CREATED", capturedEvent.getEventType());
        assertEquals(email, capturedEvent.getEmail());
        assertEquals(username, capturedEvent.getUsername());
        assertEquals(userId, capturedEvent.getUserId());
    }

    @Test
    void sendUserDeletedEvent_shouldSendCorrectEvent() {
        // Given
        Long userId = 2L;
        String email = "delete@example.com";
        String username = "Delete User";

        // When
        kafkaProducerService.sendUserDeletedEvent(userId, email, username);

        // Then
        verify(kafkaTemplate).send(eq("user-events"), eventCaptor.capture());

        UserKafkaEvent capturedEvent = eventCaptor.getValue();
        assertEquals("USER_DELETED", capturedEvent.getEventType());
        assertEquals(email, capturedEvent.getEmail());
        assertEquals(username, capturedEvent.getUsername());
        assertEquals(userId, capturedEvent.getUserId());
    }
}