package com.ct08SWA.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho API
                .authorizeExchange(exchanges -> exchanges
                        // Cho phép TẤT CẢ request đi qua lớp bảo vệ mặc định của Spring
                        // Để AuthenticationFilter của chúng ta tự xử lý sau đó
                        .anyExchange().permitAll());

        return http.build();
    }
}

