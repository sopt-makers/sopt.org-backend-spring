package sopt.org.homepage.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    MAIN_ENTITY("mainEntityCache", 10, 5),
    PROJECT_LIST("projectListCache", 100, 24 * 60),
    VISITOR("visitorCache", 10000, 24 * 60);

    private final String cacheName;
    private final int maxSize;
    private final int expireAfterMinutes;

}