package sopt.org.homepage.global.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    MAIN_ENTITY("mainEntityCache", 10, 5),
    PROJECT_LIST("projectListCache", 100, 24 * 60),
    VISITOR("visitorCache", 10000, 24 * 60),
    ADMIN_MAIN_DATA("adminCache", 1000, 24 * 60);

    private final String cacheName;
    private final int maxSize;
    private final int expireAfterMinutes;

}
