package sopt.org.homepage.application.homepage.review.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 홈페이지 후기 종류
 */
@Getter
@RequiredArgsConstructor
public enum HomepageReviewType {
    ACTIVITY("활동 후기"),
    RECRUITING("지원 후기");

    private final String displayName;
}
