package sopt.org.homepage.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    MAIN_ENTITY("mainEntityCache", 10, 5);

    private final String cacheName;
    private final int maxSize;
    private final int expireAfterMinutes;

}