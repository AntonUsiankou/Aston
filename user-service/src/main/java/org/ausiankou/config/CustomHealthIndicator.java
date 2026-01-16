package org.ausiankou.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Health health() {
        boolean dbHealthy = checkDatabase();
        boolean kafkaHealthy = checkKafka();

        if (dbHealthy && kafkaHealthy) {
            return Health.up()
                    .withDetail("database", "connected")
                    .withDetail("kafka", "connected")
                    .build();
        } else {
            return Health.down()
                    .withDetail("database", dbHealthy ? "connected" : "disconnected")
                    .withDetail("kafka", kafkaHealthy ? "connected" : "disconnected")
                    .build();
        }
    }

    private boolean checkDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkKafka() {
        try {
            kafkaTemplate.send("health-check", "ping").get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
