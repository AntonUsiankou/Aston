package org.ausiankou.integration;

import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class UserServiceKafkaIntegrationTest {

    @Autowired
    private UserService userService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void testCreateUserSendsKafkaEvent() {
        // Given
        UserRequestDto request = new UserRequestDto();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setAge(25);

        // When
        userService.createUser(request);

        // Then
        verify(kafkaTemplate, times(1)).send(any(String.class), any(Object.class));
    }
}
