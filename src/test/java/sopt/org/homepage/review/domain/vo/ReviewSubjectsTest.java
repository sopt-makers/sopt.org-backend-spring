package sopt.org.homepage.review.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.review.exception.InvalidReviewSubjectException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * ReviewSubjects 단위 테스트
 */
@DisplayName("ReviewSubjects 단위 테스트")
class ReviewSubjectsTest {

    @Test
    @DisplayName("유효한 세부 주제 리스트로 생성 성공")
    void createWithValidSubjects() {
        // given
        List<String> subjectList = List.of("세미나", "프로젝트");

        // when
        ReviewSubjects subjects = new ReviewSubjects(subjectList);

        // then
        assertThat(subjects.getValues()).containsExactly("세미나", "프로젝트");
        assertThat(subjects.size()).isEqualTo(2);
        assertThat(subjects.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("빈 리스트로 생성 성공")
    void createWithEmptyList() {
        // given
        List<String> subjectList = List.of();

        // when
        ReviewSubjects subjects = new ReviewSubjects(subjectList);

        // then
        assertThat(subjects.isEmpty()).isTrue();
        assertThat(subjects.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("null 리스트로 생성 시 예외 발생")
    void createWithNullList() {
        // given
        List<String> subjectList = null;

        // when & then
        assertThatThrownBy(() -> new ReviewSubjects(subjectList))
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessage("세부 주제 목록은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("전체 활동 카테고리에 빈 세부 주제 검증 시 예외 발생")
    void validateForActivityCategoryWithEmptySubjects() {
        // given
        ReviewCategory category = new ReviewCategory("전체 활동");
        ReviewSubjects subjects = new ReviewSubjects(List.of());

        // when & then
        assertThatThrownBy(() -> subjects.validateForCategory(category))
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessage("전체활동 카테고리는 세부 활동이 필수입니다.");
    }

    @Test
    @DisplayName("전체 활동 카테고리에 유효한 세부 주제 검증 성공")
    void validateForActivityCategoryWithValidSubjects() {
        // given
        ReviewCategory category = new ReviewCategory("전체 활동");
        ReviewSubjects subjects = new ReviewSubjects(List.of("세미나"));

        // when & then
        assertThatCode(() -> subjects.validateForCategory(category))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("서류/면접 카테고리에 빈 세부 주제 검증 시 예외 발생")
    void validateForRecruitingCategoryWithEmptySubjects() {
        // given
        ReviewCategory category = new ReviewCategory("서류/면접");
        ReviewSubjects subjects = new ReviewSubjects(List.of());

        // when & then
        assertThatThrownBy(() -> subjects.validateForCategory(category))
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessage("서류/면접 카테고리는 세부 유형이 필수입니다.");
    }

    @Test
    @DisplayName("서류/면접 카테고리에 유효한 세부 주제 검증 성공")
    void validateForRecruitingCategoryWithValidSubjects() {
        // given
        ReviewCategory category = new ReviewCategory("서류/면접");
        ReviewSubjects subjects = new ReviewSubjects(List.of("서류"));

        // when & then
        assertThatCode(() -> subjects.validateForCategory(category))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("기타 카테고리에 빈 세부 주제 검증 성공")
    void validateForOtherCategoryWithEmptySubjects() {
        // given
        ReviewCategory category = new ReviewCategory("기타");
        ReviewSubjects subjects = new ReviewSubjects(List.of());

        // when & then
        assertThatCode(() -> subjects.validateForCategory(category))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("불변 리스트 반환 확인")
    void returnsUnmodifiableList() {
        // given
        ReviewSubjects subjects = new ReviewSubjects(List.of("세미나"));

        // when
        List<String> values = subjects.getValues();

        // then
        assertThatThrownBy(() -> values.add("프로젝트"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}