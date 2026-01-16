package org.ausiankou.apigateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "User Service is temporarily unavailable");
        response.put("timestamp", System.currentTimeMillis());

        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(response)
        );
    }

    @GetMapping("/timeout")
    public Mono<ResponseEntity<Map<String, Object>>> timeoutFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.GATEWAY_TIMEOUT.value());
        response.put("message", "Request timeout");
        response.put("timestamp", System.currentTimeMillis());

        return Mono.just(
                ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(response)
        );
    }
}