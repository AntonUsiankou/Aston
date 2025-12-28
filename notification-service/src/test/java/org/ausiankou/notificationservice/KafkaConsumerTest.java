package org.ausiankou.notificationservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    void testConsumeCreateEvent() {
        // Подготовка
        UserEvent event = new UserEvent();
        event.setOperation("CREATE");
        event.setEmail("test@mail.com");
        event.setUserName("Test User");

        // Выполнение
        kafkaConsumer.listen(event);

        // Проверка
        verify(emailService, times(1))
                .sendEmail(
                        eq("test@mail.com"),
                        eq("Аккаунт создан"),
                        contains("успешно создан")
                );
    }

    @Test
    void testConsumeDeleteEvent() {
        // Подготовка
        UserEvent event = new UserEvent();
        event.setOperation("DELETE");
        event.setEmail("delete@mail.com");
        event.setUserName("Delete User");

        // Выполнение
        kafkaConsumer.listen(event);

        // Проверка
        verify(emailService, times(1))
                .sendEmail(
                        eq("delete@mail.com"),
                        eq("Аккаунт удален"),
                        contains("удалён")
                );
    }
}