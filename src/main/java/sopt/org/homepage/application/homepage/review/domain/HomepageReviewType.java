package sopt.org.homepage.application.homepage.review.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 홈페이지 후기 종류
 * <p>
 * 종류별로 최대 노출 개수를 관리한다.
 */
@Getter
@RequiredArgsConstructor
public enum HomepageReviewType {
    ACTIVITY("활동 후기"),
    RECRUITING("지원 후기");

    public static final int MAX_COUNT_PER_TYPE = 7;

    private final String displayName;

    /**
     * 종류가 지정되지 않은 경우 활동 후기로 간주 (type 필드 도입 이전 요청 호환)
     */
    public static HomepageReviewType orDefault(HomepageReviewType type) {
        return type != null ? type : ACTIVITY;
    }
}
