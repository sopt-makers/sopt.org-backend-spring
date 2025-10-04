package sopt.org.homepage.review.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.review.exception.InvalidReviewCategoryException;

/**
 * 리뷰 카테고리 타입
 *
 * 각 카테고리별 비즈니스 규칙을 캡슐화
 */
@Getter
@RequiredArgsConstructor
public enum CategoryType {

    ACTIVITY("전체 활동", true, false),
    RECRUITING("서류/면접", false, true),
    SEMINAR("세미나", false, false),
    PROJECT("프로젝트", false, false),
    STUDY("스터디", false, false),
    OTHER("기타", false, false);

    private final String displayName;
    private final boolean requiresSubActivities;
    private final boolean isRecruiting;

    /**
     * 화면 표시 이름으로 CategoryType 찾기
     *
     * @param displayName 화면 표시 이름 (예: "전체 활동")
     * @return CategoryType
     * @throws InvalidReviewCategoryException 존재하지 않는 카테고리인 경우
     */
    public static CategoryType from(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidReviewCategoryException("리뷰 카테고리는 필수입니다.");
        }

        for (CategoryType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }

        throw new InvalidReviewCategoryException(
                "유효하지 않은 카테고리입니다: " + displayName
        );
    }

    /**
     * 세부 주제가 필수인지 확인
     *
     * @return 전체 활동 또는 서류/면접인 경우 true
     */
    public boolean requiresSubject() {
        return requiresSubActivities || isRecruiting;
    }
}