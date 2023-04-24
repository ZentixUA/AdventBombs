package com.genife.adventbombs.Managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.genife.adventbombs.Managers.ConfigManager.ROCKET_USAGE_COOLDOWN;

public class CooldownManager {
    private Cache<UUID, Instant> cache;

    public CooldownManager() {
        // Настраиваем кэш при инициализации класса
        configureCache();
    }

    public void configureCache() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(ROCKET_USAGE_COOLDOWN, TimeUnit.SECONDS)
                .build();
    }

    public void setCooldown(UUID key) {
        cache.put(key, Instant.now().plus(Duration.ofSeconds(ROCKET_USAGE_COOLDOWN)));
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
