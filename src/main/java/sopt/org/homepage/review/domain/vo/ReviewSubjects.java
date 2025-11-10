package sopt.org.homepage.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sopt.org.homepage.review.exception.InvalidReviewSubjectException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 리뷰 세부 주제 Value Object
 * 레거시 getReviewSubject() 메서드의 검증 로직을 validateForCategory() 메서드로 이동
 *
 * 비즈니스 규칙:
 * - 세부 주제는 비어있을 수 있음 (카테고리에 따라)
 * - 불변 컬렉션으로 관리
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class ReviewSubjects {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"subject\"", nullable = false, columnDefinition = "text")
    private List<String> values;

    public ReviewSubjects(List<String> values) {
        validateNotNull(values);
        this.values = new ArrayList<>(values); // 방어적 복사
    }

    private void validateNotNull(List<String> values) {
        if (values == null) {
            throw new InvalidReviewSubjectException("세부 주제 목록은 null일 수 없습니다.");
        }
    }

    /**
     * 세부 주제가 비어있는지 확인
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * 세부 주제 개수 반환
     */
    public int size() {
        return values.size();
    }

    /**
     * 불변 리스트 반환
     */
    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * 특정 카테고리에 대해 유효한 세부 주제인지 검증
     */
    public void validateForCategory(ReviewCategory category) {
        if (category.requiresSubActivities() && isEmpty()) {
            throw new InvalidReviewSubjectException(
                    "전체활동 카테고리는 세부 활동이 필수입니다."
            );
        }

        if (category.isRecruitingCategory() && isEmpty()) {
            throw new InvalidReviewSubjectException(
                    "서류/면접 카테고리는 세부 유형이 필수입니다."
            );
        }
    }
}