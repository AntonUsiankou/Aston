package org.ausiankou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication(
        exclude = {GatewayAutoConfiguration.class} // Исключаем Gateway для REST контроллеров
)
@EnableEurekaServer
@EnableDiscoveryClient
@EnableConfigServer
public class App
{
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
