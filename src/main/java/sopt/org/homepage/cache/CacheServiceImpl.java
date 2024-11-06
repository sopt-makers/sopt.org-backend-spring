package sopt.org.homepage.cache;

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