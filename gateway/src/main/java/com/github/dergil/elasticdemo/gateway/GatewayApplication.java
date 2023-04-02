package com.github.dergil.elasticdemo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

//    Routes HTTP requests to their respective microservices
//    The domain is determined by their name in the docker network, in this case the name of their docker-compose entry
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/car/*")
                        .uri("http://car:8081"))
                .route(p -> p
                        .path("/car/tax/*")
                        .uri("http://car:8081"))
                .route(p -> p
                        .path("/car")
                        .uri("http://car:8081"))
                .route(p -> p
                        .path("/calculate")
                        .uri("http://calculator:8084"))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
