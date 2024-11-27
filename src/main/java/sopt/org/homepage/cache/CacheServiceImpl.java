package sopt.org.homepage.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import sopt.org.homepage.common.constants.CacheType;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    @Override
    public void put(CacheType cacheType, String key, Object value) {
        Cache cache = cacheManager.getCache(cacheType.getCacheName());
        if (cache != null) {
            cache.put(key, value);
            log.debug("Cached value for key: {} in cache: {}", key, cacheType.getCacheName());
        }
    }

    @Override
    public <T> T get(CacheType cacheType, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheType.getCacheName());
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                return type.cast(wrapper.get());
            }
        }
        return null;
    }

    @Override
    public <T> T get(CacheType cacheType, String key, TypeReference<T> type) {
        Cache cache = cacheManager.getCache(cacheType.getCacheName());
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                log.info("Cache HIT: {}", key);
                Object value = wrapper.get();
                try {
                    return objectMapper.convertValue(value, type);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Failed to convert cache value", e);
                }
            }
        }
        log.info("Cache MISS: {}", key);
        return null;
    }


    @Override
    public void evict(CacheType cacheType, String key) {
        Cache cache = cacheManager.getCache(cacheType.getCacheName());
        if (cache != null) {
            cache.evict(key);
            log.debug("Evicted key: {} from cache: {}", key, cacheType.getCacheName());
        }
    }

    @Override
    public void clearCache(CacheType cacheType) {
        Cache cache = cacheManager.getCache(cacheType.getCacheName());
        if (cache != null) {
            cache.clear();
            log.debug("Cleared all data from cache: {}", cacheType.getCacheName());
        }
    }
}