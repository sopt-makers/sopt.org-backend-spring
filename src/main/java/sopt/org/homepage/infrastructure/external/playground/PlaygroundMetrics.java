package sopt.org.homepage.infrastructure.external.playground;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Playground 외부 API 관련 Micrometer 메트릭 이름 상수
 * <p>
 * 사용 가능한 메트릭: {@code playground.cache.hit} — 캐시 히트 횟수 {@code playground.cache.miss} — 캐시 미스 횟수 (= 외부 API 호출 트리거)
 * {@code playground.api.call.count} — Playground API 실제 호출 횟수 (페이지네이션 단위) {@code playground.api.call.duration} —
 * Playground API 전체 호출 소요 시간< Actuator 조회 예시: curl http://localhost:8080/actuator/metrics/playground.cache.hit curl
 * http://localhost:8080/actuator/metrics/playground.api.call.duration
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlaygroundMetrics {

    private static final String PREFIX = "playground";

    /**
     * 프로젝트 목록 캐시 히트 카운터
     */
    public static final String CACHE_HIT = PREFIX + ".cache.hit";

    /**
     * 프로젝트 목록 캐시 미스 카운터
     */
    public static final String CACHE_MISS = PREFIX + ".cache.miss";

    /**
     * Playground API 호출 카운터 (커서 기반 페이지네이션 1회 = 1 카운트)
     */
    public static final String API_CALL_COUNT = PREFIX + ".api.call.count";

    /**
     * Playground API 전체 호출 소요 시간 타이머 (fetchAllFromApi 전체)
     */
    public static final String API_CALL_DURATION = PREFIX + ".api.call.duration";
}
