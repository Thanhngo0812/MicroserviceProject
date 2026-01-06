package com.ct08SWA.userservice.usercontainer.redis;
import com.ct08SWA.userservice.userapplicationservice.ports.outputports.UserSecurityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSecurityPortImpl implements UserSecurityPort {
        private final RedisTemplate<String, String> redisTemplate; // <-- Chứa RedisTemplate
        private static final Duration BLACKLIST_TTL = Duration.ofMinutes(30);
        private static final String BLACKLIST_KEY_PREFIX = "blacklist:";
        private static final String CACHE_INFO_KEY_PREFIX = "user-info::"; // Key của Order Service

        @Override
        public void addToBlacklist(String userId) {
            String key = BLACKLIST_KEY_PREFIX + userId;
            // Ghi vào Redis với TTL 30 phút
            redisTemplate.opsForValue().set(key, "blocked", BLACKLIST_TTL);
            log.warn("Đã thêm user {} vào Blacklist. TTL: {} phút.", userId, BLACKLIST_TTL.toMinutes());
        }

        @Override
        public void removeFromBlacklist(String userId) {
            String key = BLACKLIST_KEY_PREFIX + userId;
            Boolean deleted = redisTemplate.delete(key);
            log.info("Đã xóa Blacklist cho user {}. Thành công: {}", userId, deleted);
        }
    }
