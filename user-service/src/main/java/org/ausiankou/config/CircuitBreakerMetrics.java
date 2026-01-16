package org.ausiankou.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CircuitBreakerMetrics implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;
    private final Map<String, CircuitBreaker.Metrics> metricsCache = new ConcurrentHashMap<>();

    public CircuitBreakerMetrics(CircuitBreakerRegistry circuitBreakerRegistry,
                                 MeterRegistry meterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        circuitBreakerRegistry.getAllCircuitBreakers()
                .forEach(circuitBreaker -> {
                    String name = circuitBreaker.getName();

                    metricsCache.put(name, circuitBreaker.getMetrics());

                    meterRegistry.gauge("circuitbreaker." + name + ".state",
                            circuitBreaker,
                            cb -> cb.getState().getOrder());

                    meterRegistry.gauge("circuitbreaker." + name + ".failure_rate",
                            circuitBreaker,
                            cb -> cb.getMetrics().getFailureRate());
                });
    }

    @Override
    public Health health() {
        boolean allHealthy = circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .allMatch(cb -> cb.getState() == CircuitBreaker.State.CLOSED);

        if (allHealthy) {
            return Health.up()
                    .withDetail("circuit_breakers",
                            circuitBreakerRegistry.getAllCircuitBreakers().size())
                    .build();
        } else {
            return Health.down()
                    .withDetail("circuit_breakers_open",
                            circuitBreakerRegistry.getAllCircuitBreakers()
                                    .stream()
                                    .filter(cb -> cb.getState() != CircuitBreaker.State.CLOSED)
                                    .count())
                    .build();
        }
    }
}
