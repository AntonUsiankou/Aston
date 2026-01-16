package org.ausiankou.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("userServiceCB")
                                        .setFallbackUri("forward:/fallback/user-service"))
                                .rewritePath("/api/users/(?<segment>.*)", "/api/v1/users/${segment}")
                                .addRequestHeader("X-Gateway-Request", "true"))
                        .uri("lb://USER-SERVICE"))

                .route("user-service-swagger", r -> r
                        .path("/swagger-ui/**", "/v3/api-docs/**")
                        .uri("lb://USER-SERVICE"))

                .build();
    }
}
