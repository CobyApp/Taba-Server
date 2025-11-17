package com.taba.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 토큰 블랙리스트 서비스
 * Redis는 선택사항입니다. Redis가 없어도 애플리케이션이 정상 작동합니다.
 * Redis가 없으면 블랙리스트 기능만 비활성화됩니다.
 */
@Slf4j
@Service
public class TokenBlacklistService {

    private final Optional<RedisTemplate<String, String>> redisTemplateOptional;
    private static final String BLACKLIST_PREFIX = "blacklist:token:";
    private volatile boolean redisAvailable = true;

    @Autowired(required = false)
    public TokenBlacklistService(Optional<RedisTemplate<String, String>> redisTemplateOptional) {
        this.redisTemplateOptional = redisTemplateOptional;
        if (redisTemplateOptional.isEmpty()) {
            log.info("Redis is not configured. Token blacklist feature will be disabled.");
            redisAvailable = false;
        } else {
            log.info("Redis is configured. Token blacklist feature is enabled.");
        }
    }

    public void addToBlacklist(String token, long expirationTimeMillis) {
        if (!redisAvailable || redisTemplateOptional.isEmpty()) {
            log.debug("Redis is not available, skipping token blacklist");
            return;
        }

        try {
            RedisTemplate<String, String> redisTemplate = redisTemplateOptional.get();
            long ttl = expirationTimeMillis - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + token, 
                        "true", 
                        ttl, 
                        TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            log.warn("Failed to add token to blacklist (Redis may be unavailable): {}", e.getMessage());
            redisAvailable = false;
        }
    }

    public boolean isBlacklisted(String token) {
        if (!redisAvailable || redisTemplateOptional.isEmpty()) {
            return false; // Redis가 없으면 블랙리스트 체크 불가 (토큰 허용)
        }

        try {
            RedisTemplate<String, String> redisTemplate = redisTemplateOptional.get();
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        } catch (Exception e) {
            log.debug("Failed to check token blacklist (Redis may be unavailable): {}", e.getMessage());
            redisAvailable = false;
            return false; // Redis 연결 실패 시 토큰 허용
        }
    }
}

