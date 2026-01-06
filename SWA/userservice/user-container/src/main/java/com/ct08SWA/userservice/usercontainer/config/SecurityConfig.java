package com.ct08SWA.userservice.usercontainer.config;


import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho REST API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll() // hoặc giới hạn cho admin client
                        // Cho phép truy cập công khai vào endpoint auth (Login/Register)
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        // Các request khác bắt buộc phải có token (authenticated)
                        .anyRequest().permitAll()
                );

        // (Lưu ý: Bạn sẽ cần thêm JwtAuthenticationFilter vào đây nếu muốn
        //  UserService tự xác thực token của chính nó, nhưng hiện tại
        //  chúng ta chỉ cần nó sinh token để Gateway dùng)

        return http.build();
    }

    // Bean mã hóa mật khẩu (dùng cho PasswordEncoderImpl)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}