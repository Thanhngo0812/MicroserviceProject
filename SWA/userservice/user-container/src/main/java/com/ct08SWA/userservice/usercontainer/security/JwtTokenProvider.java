package com.ct08SWA.userservice.usercontainer.security;



import com.ct08SWA.userservice.userapplicationservice.ports.outputports.TokenProviderPort;
import com.ct08SWA.userservice.userdomaincore.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    // Lấy secret key từ application.properties
    // (Key phải dài ít nhất 256 bit cho HS256)
    @Value("${jwt.secret}")
    private String secret;

    // Thời gian hết hạn (ms)
    @Value("${jwt.expiration}")
    private long expiration;

    @Override
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId().getValue().toString());
        // Thêm các thông tin khác vào payload nếu cần

        return createToken(claims, user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}