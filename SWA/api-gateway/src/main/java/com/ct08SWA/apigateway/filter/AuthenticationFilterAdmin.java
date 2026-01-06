package com.ct08SWA.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;

@Component
public class AuthenticationFilterAdmin extends AbstractGatewayFilterFactory<AuthenticationFilterAdmin.Config> {

    @Value("${jwt.secret}")
    private String secret;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public AuthenticationFilterAdmin(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Kiểm tra Header Authorization
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            // 2. Lấy Token
            String token = authHeader.substring(7);

            // 3. Validate Token
            try {
                validateToken(token);
                Claims claims = getAllClaimsFromToken(token);
                String userId = claims.get("userId", String.class);

                if (userId == null) {
                    return onError(exchange, "Invalid Token: Missing User ID", HttpStatus.UNAUTHORIZED);
                }
                System.out.println(claims.get("role", String.class));
                if(!"ADMIN".equals(claims.get("role", String.class))){
                    return onError(exchange, "you don't have permission", HttpStatus.FORBIDDEN);

                }
                String blacklistKey = "blacklist:" + userId;
                return redisTemplate.hasKey(blacklistKey)
                        .flatMap(isBlacklisted -> {
                            if (isBlacklisted) {
                                // Nếu có trong blacklist -> Chặn luôn (403 Forbidden)
                                return onError(exchange, "Account is blocked/locked!", HttpStatus.FORBIDDEN);
                            } else {
                                // Nếu không bị khóa -> Gắn Header và cho qua
                                var request = exchange.getRequest().mutate()
                                        .header("X-User-Id", userId)
                                        .build();
                                return chain.filter(exchange.mutate().request(request).build());
                            }
                        });
                // (Tùy chọn) Lấy thông tin từ Token và gán vào Header để các service sau dùng
                // Claims claims = getClaims(token);
                // exchange.getRequest().mutate()
                //     .header("X-User-Id", claims.get("userId").toString())
                //     .header("X-User-Role", claims.get("role").toString())
                //     .build();

            } catch (ExpiredJwtException e) {
                // 1. Bắt lỗi hết hạn riêng
                return onError(exchange, "Token has expired", HttpStatus.UNAUTHORIZED);

            } catch (Exception e) {
                // 3. Bắt các lỗi còn lại
                return onError(exchange, "Authentication failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        // 1. Đặt Content-Type là JSON để Frontend dễ xử lý
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // 2. Tạo nội dung lỗi (JSON thủ công cho đơn giản)
        // Nếu err có ký tự đặc biệt bạn nên dùng ObjectMapper để convert cho chuẩn
        String errorMessage = String.format("{\"error\": \"%s\", \"status\": %d}", err, httpStatus.value());

        // 3. Ghi dữ liệu vào response
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Put configuration properties here
    }
}
