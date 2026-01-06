package com.ct08SWA.apigateway.config;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import java.util.Objects;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        // Cách 1: Limit theo IP Address
        return exchange -> Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                        .getAddress().getHostAddress()
        );

        // Cách 2: Limit theo User ID trong Header (nếu có)
        // return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("X-User-ID"));
    }
}
