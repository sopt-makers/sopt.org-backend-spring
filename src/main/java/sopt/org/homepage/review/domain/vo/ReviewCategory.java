package sopt.org.homepage.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.review.exception.InvalidReviewCategoryException;

/**
 * 리뷰 카테고리 Value Object
 * 레거시 getReviewSubject() 메서드에 있는 검증 로직을 VO로 캡슐화
 *
 * 비즈니스 규칙:
 * - "전체 활동" 카테고리는 반드시 세부 활동(subject)이 필요함
 * - "서류/면접" 카테고리는 반드시 하나의 세부 유형(subject)이 필요함
 * - 그 외 카테고리는 세부 활동이 필요 없음
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class ReviewCategory {

    @Column(name = "\"category\"", nullable = false, length = 20)
    private String value;

    public ReviewCategory(String value) {
        validateNotNull(value);
        this.value = value;
    }

    private void validateNotNull(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidReviewCategoryException("리뷰 카테고리는 필수입니다.");
        }
    }

    /**
     * 세부 활동이 필수인 카테고리인지 확인
     * "전체 활동" 카테고리인 경우 true 반환
     */
    public boolean requiresSubActivities() {
        return "전체 활동".equals(value);
    }

    /**
     * 서류/면접 카테고리인지 확인
     */
    public boolean isRecruitingCategory() {
        return "서류/면접".equals(value);
    }

    /**
     * 세부 유형이 필요한 카테고리인지 확인
     */
    public boolean requiresSubject() {
        return requiresSubActivities() || isRecruitingCategory();
    }
}