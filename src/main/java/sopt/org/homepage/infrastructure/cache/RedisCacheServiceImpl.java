package sopt.org.homepage.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sopt.org.homepage.global.common.constants.CacheType;

/**
 * Redis 기반 CacheService 구현체 Lambda 환경에서 인스턴스 간 캐시를 공유하기 위해 외부 Redis 서버를 사용
 * <p>
 * 기존 CacheServiceImpl(Caffeine)과 동일한 인터페이스를 구현하므로 PlaygroundServiceImpl 등 호출부 코드 변경이 필요 없다.
 * <p>
 * Redis Key 규칙: {@code {cacheName}:{key}} 예: {@code projectListCache:all_projects}
 */
@Service
@Profile("lambda-dev")
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis Key 생성 CacheType의 cacheName과 key를 조합하여 Redis key를 생성한다. 예: projectListCache:all_projects
     */
    private String buildKey(CacheType cacheType, String key) {
        return cacheType.getCacheName() + ":" + key;
    }

    /**
     * CacheType의 TTL을 Duration으로 변환
     */
    private Duration getTtl(CacheType cacheType) {
        return Duration.ofMinutes(cacheType.getExpireAfterMinutes());
    }

    @Override
    public void put(CacheType cacheType, String key, Object value) {
        String redisKey = buildKey(cacheType, key);
        Duration ttl = getTtl(cacheType);

        try {
            redisTemplate.opsForValue().set(redisKey, value, ttl);
            log.debug("Redis SET: {} (TTL: {}min)", redisKey, cacheType.getExpireAfterMinutes());
        } catch (Exception e) {
            log.error("Redis SET failed: {} — {}", redisKey, e.getMessage());
            // Redis 장애 시에도 애플리케이션은 정상 동작해야 함 (캐시 미스로 처리)
        }
    }

    @Override
    public <T> T get(CacheType cacheType, String key, Class<T> type) {
        String redisKey = buildKey(cacheType, key);

        try {
            Object value = redisTemplate.opsForValue().get(redisKey);
            if (value != null) {
                log.info("Cache HIT: {}", key);
                return objectMapper.convertValue(value, type);
            }
        } catch (Exception e) {
            log.error("Redis GET failed: {} — {}", redisKey, e.getMessage());
        }

        log.info("Cache MISS: {}", key);
        return null;
    }

    @Override
    public <T> T get(CacheType cacheType, String key, TypeReference<T> type) {
        String redisKey = buildKey(cacheType, key);

        try {
            Object value = redisTemplate.opsForValue().get(redisKey);
            if (value != null) {
                log.info("Cache HIT: {}", key);
                return objectMapper.convertValue(value, type);
            }
        } catch (Exception e) {
            log.error("Redis GET failed: {} — {}", redisKey, e.getMessage());
        }

        log.info("Cache MISS: {}", key);
        return null;
    }

    @Override
    public void evict(CacheType cacheType, String key) {
        String redisKey = buildKey(cacheType, key);

        try {
            Boolean deleted = redisTemplate.delete(redisKey);
            log.debug("Redis DEL: {} (result: {})", redisKey, deleted);
        } catch (Exception e) {
            log.error("Redis DEL failed: {} — {}", redisKey, e.getMessage());
        }
    }

    @Override
    public void clearCache(CacheType cacheType) {
        String pattern = cacheType.getCacheName() + ":*";

        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long deletedCount = redisTemplate.delete(keys);
                log.debug("Redis CLEAR: {} keys deleted for pattern: {}", deletedCount, pattern);
            }
        } catch (Exception e) {
            log.error("Redis CLEAR failed: pattern {} — {}", pattern, e.getMessage());
        }
    }
}
