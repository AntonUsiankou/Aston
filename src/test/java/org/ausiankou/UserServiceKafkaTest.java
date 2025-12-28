package org.ausiankou;

import org.ausiankou.config.KafkaProducerService;
import org.ausiankou.event.UserEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Test
    void testSendEvent() {
        // Arrange
        UserEvent event = UserEvent.builder()
                .operation("CREATE")
                .email("test@test.com")
                .userName("Test")
                .userId(1L)
                .build();

        // Act
        kafkaProducerService.sendUserEvent(event);

        // Assert
        verify(kafkaProducerService, times(1))
                .sendUserEvent(event);
    }
}
