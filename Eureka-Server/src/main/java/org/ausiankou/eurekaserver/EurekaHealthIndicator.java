package org.ausiankou.eurekaserver;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class EurekaHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "eureka-server")
                .withDetail("status", "registered")
                .build();
    }
}
