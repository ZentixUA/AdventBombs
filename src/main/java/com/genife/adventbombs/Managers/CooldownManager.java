package com.genife.adventbombs.Managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    public static final int DEFAULT_COOLDOWN = 5;
    private final Cache<UUID, Instant> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(DEFAULT_COOLDOWN, TimeUnit.SECONDS)
            .build();

    public void setCooldown(UUID key, Duration duration) {
        cache.put(key, Instant.now().plus(duration));
    }

    public Duration getRemainingCooldown(UUID key) {
        Instant cooldown = cache.getIfPresent(key);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            return Duration.ZERO;
        }
    }
}
