package sopt.org.homepage.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.review.exception.InvalidReviewCategoryException;

/**
 * 리뷰 카테고리 Value Object
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

    @Enumerated(EnumType.STRING)
    @Column(name = "\"category\"", nullable = false, length = 20)
    private CategoryType type;

    /**
     * 화면 표시 이름으로 카테고리 생성
     *
     * @param displayName 화면 표시 이름 (예: "전체 활동")
     */
    public ReviewCategory(String displayName) {
        this.type = CategoryType.from(displayName);
    }

    /**
     * CategoryType으로 직접 생성
     *
     * @param type CategoryType enum
     */
    public ReviewCategory(CategoryType type) {
        if (type == null) {
            throw new InvalidReviewCategoryException("리뷰 카테고리는 필수입니다.");
        }
        this.type = type;
    }

    /**
     * 세부 활동이 필수인 카테고리인지 확인
     * "전체 활동" 카테고리인 경우 true 반환
     */
    public boolean requiresSubActivities() {
        return type.isRequiresSubActivities();
    }

    /**
     * 서류/면접 카테고리인지 확인
     */
    public boolean isRecruitingCategory() {
        return type.isRecruiting();
    }

    /**
     * 세부 유형이 필요한 카테고리인지 확인
     */
    public boolean requiresSubject() {
        return type.requiresSubject();
    }

    /**
     * 화면 표시용 이름 반환
     *
     * @return 화면 표시 이름 (예: "전체 활동")
     */
    public String getValue() {
        return type.getDisplayName();
    }

    /**
     * Enum 값 반환 (내부 사용)
     */
    CategoryType getType() {
        return type;
    }
}