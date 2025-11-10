package sopt.org.homepage.review.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sopt.org.homepage.review.exception.InvalidReviewCategoryException;

import static org.assertj.core.api.Assertions.*;

/**
 * ReviewCategory 단위 테스트
 *
 * Mock 없이 순수 Java 객체만으로 테스트 가능
 */
@DisplayName("ReviewCategory 단위 테스트")
class ReviewCategoryTest {

    @Test
    @DisplayName("유효한 카테고리로 생성 성공")
    void createWithValidCategory() {
        // given
        String categoryName = "전체 활동";

        // when
        ReviewCategory category = new ReviewCategory(categoryName);

        // then
        assertThat(category.getValue()).isEqualTo("전체 활동");
        assertThat(category.getType()).isEqualTo(CategoryType.ACTIVITY);
    }

    @Test
    @DisplayName("CategoryType으로 직접 생성 성공")
    void createWithCategoryType() {
        // given
        CategoryType type = CategoryType.ACTIVITY;

        // when
        ReviewCategory category = new ReviewCategory(type);

        // then
        assertThat(category.getType()).isEqualTo(CategoryType.ACTIVITY);
        assertThat(category.getValue()).isEqualTo("전체 활동");
    }

    @Test
    @DisplayName("null 카테고리로 생성 시 예외 발생")
    void createWithNullCategory() {
        // given
        String categoryValue = null;

        // when & then
        assertThatThrownBy(() -> new ReviewCategory(categoryValue))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessage("리뷰 카테고리는 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열 카테고리로 생성 시 예외 발생")
    void createWithBlankCategory() {
        // given
        String categoryValue = "   ";

        // when & then
        assertThatThrownBy(() -> new ReviewCategory(categoryValue))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessage("리뷰 카테고리는 필수입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 카테고리명으로 생성 시 예외 발생")
    void createWithInvalidCategory() {
        // given
        String invalidCategory = "존재하지않는카테고리";

        // when & then
        assertThatThrownBy(() -> new ReviewCategory(invalidCategory))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessageContaining("유효하지 않은 카테고리입니다");
    }

    @Test
    @DisplayName("null CategoryType으로 생성 시 예외 발생")
    void createWithNullCategoryType() {
        // given
        CategoryType type = null;

        // when & then
        assertThatThrownBy(() -> new ReviewCategory(type))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessage("리뷰 카테고리는 필수입니다.");
    }

    @Test
    @DisplayName("전체 활동 카테고리는 세부 활동이 필수임을 확인")
    void requiresSubActivitiesForActivityCategory() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);

        // when
        boolean result = category.requiresSubActivities();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("서류/면접 카테고리는 리크루팅 카테고리임을 확인")
    void isRecruitingCategoryForRecruitingCategory() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.RECRUITING);

        // when
        boolean result = category.isRecruitingCategory();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"세미나", "프로젝트", "스터디", "기타"})
    @DisplayName("기타 카테고리는 세부 활동이 불필요")
    void doesNotRequireSubActivitiesForOtherCategories(String categoryName) {
        // given
        ReviewCategory category = new ReviewCategory(categoryName);

        // when
        boolean requiresSubActivities = category.requiresSubActivities();
        boolean isRecruiting = category.isRecruitingCategory();

        // then
        assertThat(requiresSubActivities).isFalse();
        assertThat(isRecruiting).isFalse();
    }

    @Test
    @DisplayName("전체 활동 카테고리는 세부 주제가 필수")
    void requiresSubjectForActivityCategory() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);

        // when
        boolean result = category.requiresSubject();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("서류/면접 카테고리는 세부 주제가 필수")
    void requiresSubjectForRecruitingCategory() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.RECRUITING);

        // when
        boolean result = category.requiresSubject();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("세미나 카테고리는 세부 주제가 불필요")
    void doesNotRequireSubjectForSeminarCategory() {
        // given
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);

        // when
        boolean result = category.requiresSubject();

        // then
        assertThat(result).isFalse();
    }
}