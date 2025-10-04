package sopt.org.homepage.review.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sopt.org.homepage.review.exception.InvalidReviewCategoryException;

import static org.assertj.core.api.Assertions.*;

/**
 * CategoryType Enum 단위 테스트
 */
@DisplayName("CategoryType 단위 테스트")
class CategoryTypeTest {

    @ParameterizedTest
    @CsvSource({
            "전체 활동, ACTIVITY",
            "서류/면접, RECRUITING",
            "세미나, SEMINAR",
            "프로젝트, PROJECT",
            "스터디, STUDY",
            "기타, OTHER"
    })
    @DisplayName("화면 표시 이름으로 CategoryType 조회 성공")
    void fromDisplayName(String displayName, CategoryType expectedType) {
        // when
        CategoryType result = CategoryType.from(displayName);

        // then
        assertThat(result).isEqualTo(expectedType);
        assertThat(result.getDisplayName()).isEqualTo(displayName);
    }

    @Test
    @DisplayName("null 입력 시 예외 발생")
    void fromWithNull() {
        // when & then
        assertThatThrownBy(() -> CategoryType.from(null))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessage("리뷰 카테고리는 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열 입력 시 예외 발생")
    void fromWithBlank() {
        // when & then
        assertThatThrownBy(() -> CategoryType.from("   "))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessage("리뷰 카테고리는 필수입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리명 입력 시 예외 발생")
    void fromWithInvalidName() {
        // when & then
        assertThatThrownBy(() -> CategoryType.from("존재하지않는카테고리"))
                .isInstanceOf(InvalidReviewCategoryException.class)
                .hasMessageContaining("유효하지 않은 카테고리입니다");
    }

    @Test
    @DisplayName("ACTIVITY는 세부 활동이 필수")
    void activityRequiresSubActivities() {
        // given
        CategoryType type = CategoryType.ACTIVITY;

        // when & then
        assertThat(type.isRequiresSubActivities()).isTrue();
        assertThat(type.isRecruiting()).isFalse();
        assertThat(type.requiresSubject()).isTrue();
    }

    @Test
    @DisplayName("RECRUITING은 세부 유형이 필수")
    void recruitingRequiresSubject() {
        // given
        CategoryType type = CategoryType.RECRUITING;

        // when & then
        assertThat(type.isRequiresSubActivities()).isFalse();
        assertThat(type.isRecruiting()).isTrue();
        assertThat(type.requiresSubject()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "SEMINAR",
            "PROJECT",
            "STUDY",
            "OTHER"
    })
    @DisplayName("기타 카테고리는 세부 주제 불필요")
    void otherCategoriesDoNotRequireSubject(CategoryType type) {
        // when & then
        assertThat(type.isRequiresSubActivities()).isFalse();
        assertThat(type.isRecruiting()).isFalse();
        assertThat(type.requiresSubject()).isFalse();
    }

    @Test
    @DisplayName("모든 CategoryType은 displayName을 가짐")
    void allTypesHaveDisplayName() {
        // given & when & then
        for (CategoryType type : CategoryType.values()) {
            assertThat(type.getDisplayName())
                    .isNotNull()
                    .isNotBlank();
        }
    }
}