package sopt.org.homepage.visitor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sopt.org.homepage.common.constants.CacheType;


import java.util.Objects;
import java.util.Set;
import sopt.org.homepage.visitor.dto.GetTodayVisitorResponseDto;
import sopt.org.homepage.visitor.dto.VisitorCountUpResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {
    private final CacheManager cacheManager;
    private static final String VISITOR_PREFIX = "visitor-";

    public VisitorCountUpResponseDto visitorCountUp(HttpServletRequest request) {
        try {
            String clientIp = extractClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String uniqueUserInfo = VISITOR_PREFIX + userAgent + clientIp;

            Objects.requireNonNull(cacheManager.getCache(CacheType.VISITOR.getCacheName()))
                    .put(uniqueUserInfo, "visited");

            return VisitorCountUpResponseDto.of("Success");
        } catch (Exception e) {
            log.error("Failed to count up visitor", e);
            return VisitorCountUpResponseDto.of("fail");
        }
    }

    public GetTodayVisitorResponseDto getTodayVisitor() {
        try {
            Set<String> keys = getCacheKeys();
            long visitorCount = keys.stream()
                    .filter(key -> key.startsWith(VISITOR_PREFIX))
                    .count();

            return GetTodayVisitorResponseDto.of((int) visitorCount);
        } catch (Exception e) {
            log.error("Failed to get today's visitor count", e);
            return GetTodayVisitorResponseDto.of(0);
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void visitorReset() {
        try {
            Set<String> keys = getCacheKeys();
            keys.stream()
                    .filter(key -> key.startsWith(VISITOR_PREFIX))
                    .forEach(key -> Objects.requireNonNull(cacheManager.getCache(CacheType.VISITOR.getCacheName())).evict(key));

            log.info("Visitor Reset CronJob Complete");
        } catch (Exception e) {
            log.error("Failed to reset visitors", e);
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Real-IP");
        if (clientIp == null) {
            clientIp = request.getHeader("X-Forwarded-For");
        }
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private Set<String> getCacheKeys() {
        Set<Object> keys = ((CaffeineCache) Objects.requireNonNull(cacheManager.getCache(CacheType.VISITOR.getCacheName())))
                .getNativeCache().asMap().keySet();

        return keys.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}