package org.ausiankou.service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ausiankou.dto.UserResponseDto;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceWithCircuitBreaker {

    private final UserService userService;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetUser")
    @Retry(name = "userService", fallbackMethod = "fallbackGetUser")
    public UserResponseDto getUserByIdWithCircuitBreaker(Long id) {
        return userService.getUserById(id);
    }

    private UserResponseDto fallbackGetUser(Long id, Throwable throwable) {
        log.warn("Circuit Breaker fallback triggered for getUserById({}). Error: {}",
                id, throwable.getMessage());

        return UserResponseDto.builder()
                .id(id)
                .name("Service Temporarily Unavailable")
                .email("fallback@example.com")
                .age(0)
                .build();
    }
}