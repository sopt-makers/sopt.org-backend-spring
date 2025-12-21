package sopt.org.homepage.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import sopt.org.homepage.global.common.constants.CacheType;

public interface CacheService {
    void put(CacheType cacheType, String key, Object value);

    <T> T get(CacheType cacheType, String key, Class<T> type);

    <T> T get(CacheType cacheType, String key, TypeReference<T> type);

    void evict(CacheType cacheType, String key);

    void clearCache(CacheType cacheType);
}
