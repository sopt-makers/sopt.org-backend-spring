package sopt.org.homepage.review.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.review.domain.vo.*;
import sopt.org.homepage.review.exception.DuplicateReviewUrlException;
import sopt.org.homepage.review.exception.InvalidReviewSubjectException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Review 엔티티 단위 테스트
 *
 * Rich Domain Model의 핵심:
 * - Mock 없이 순수 도메인 로직 테스트
 * - 팩토리 메서드 검증
 * - 비즈니스 규칙 검증
 */
@DisplayName("Review 엔티티 단위 테스트")
class ReviewTest {

    // === 팩토리 메서드 테스트 ===

    @Test
    @DisplayName("유효한 데이터로 리뷰 생성 성공")
    void createReviewWithValidData() {
        // given
        ReviewContent content = new ReviewContent(
                "SOPT 34기 활동 후기",
                "정말 유익한 활동이었습니다.",
                "https://example.com/thumbnail.jpg",
                "Medium"
        );
        ReviewAuthor author = new ReviewAuthor("홍길동", "https://example.com/profile.jpg");
        Integer generation = 34;
        Part part = Part.SERVER;
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);
        ReviewSubjects subjects = new ReviewSubjects(List.of("세미나", "프로젝트"));
        ReviewUrl url = new ReviewUrl("https://medium.com/@sopt/review");

        // when
        Review review = Review.create(content, author, generation, part, category, subjects, url);

        // then
        assertThat(review).isNotNull();
        assertThat(review.getTitle()).isEqualTo("SOPT 34기 활동 후기");
        assertThat(review.getDescription()).isEqualTo("정말 유익한 활동이었습니다.");
        assertThat(review.getAuthorName()).isEqualTo("홍길동");
        assertThat(review.getGeneration()).isEqualTo(34);
        assertThat(review.getPart()).isEqualTo(Part.SERVER);
        assertThat(review.getCategoryValue()).isEqualTo("전체 활동");
        assertThat(review.getSubjectValues()).containsExactly("세미나", "프로젝트");
        assertThat(review.getUrlValue()).isEqualTo("https://medium.com/@sopt/review");
    }

    @Test
    @DisplayName("전체 활동 카테고리에 세부 활동이 있으면 생성 성공")
    void createActivityReviewWithSubjects() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);
        ReviewSubjects subjects = new ReviewSubjects(List.of("세미나"));
        ReviewUrl url = createValidUrl();

        // when
        Review review = Review.create(content, author, 34, Part.SERVER, category, subjects, url);

        // then
        assertThat(review).isNotNull();
        assertThat(review.getCategoryValue()).isEqualTo("전체 활동");
        assertThat(review.getSubjectValues()).containsExactly("세미나");
    }

    @Test
    @DisplayName("서류/면접 카테고리에 세부 유형이 있으면 생성 성공")
    void createRecruitingReviewWithSubject() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.RECRUITING);
        ReviewSubjects subjects = new ReviewSubjects(List.of("서류"));
        ReviewUrl url = createValidUrl();

        // when
        Review review = Review.create(content, author, 34, Part.SERVER, category, subjects, url);

        // then
        assertThat(review).isNotNull();
        assertThat(review.getCategoryValue()).isEqualTo("서류/면접");
        assertThat(review.getSubjectValues()).containsExactly("서류");
    }

    @Test
    @DisplayName("기타 카테고리에 빈 세부 주제로 생성 성공")
    void createOtherCategoryReviewWithoutSubjects() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();

        // when
        Review review = Review.create(content, author, 34, Part.SERVER, category, subjects, url);

        // then
        assertThat(review).isNotNull();
        assertThat(review.getCategoryValue()).isEqualTo("세미나");
        assertThat(review.getSubjectValues()).isEmpty();
    }

    // === 비즈니스 규칙 검증 테스트 ===

    @Test
    @DisplayName("전체 활동 카테고리에 빈 세부 활동으로 생성 시 예외 발생")
    void createActivityReviewWithoutSubjectsThrowsException() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);
        ReviewSubjects emptySubjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();

        // when & then
        assertThatThrownBy(() ->
                Review.create(content, author, 34, Part.SERVER, category, emptySubjects, url)
        )
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessageContaining("전체활동 카테고리는 세부 활동이 필수입니다");
    }

    @Test
    @DisplayName("서류/면접 카테고리에 빈 세부 유형으로 생성 시 예외 발생")
    void createRecruitingReviewWithoutSubjectThrowsException() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.RECRUITING);
        ReviewSubjects emptySubjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();

        // when & then
        assertThatThrownBy(() ->
                Review.create(content, author, 34, Part.SERVER, category, emptySubjects, url)
        )
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessageContaining("서류/면접 카테고리는 세부 유형이 필수입니다");
    }

    // === 기수 검증 테스트 ===

    @Test
    @DisplayName("null 기수로 생성 시 예외 발생")
    void createWithNullGenerationThrowsException() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();
        Integer nullGeneration = null;

        // when & then
        assertThatThrownBy(() ->
                Review.create(content, author, nullGeneration, Part.SERVER, category, subjects, url)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기수는 1 이상이어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("0 이하의 기수로 생성 시 예외 발생")
    void createWithInvalidGenerationThrowsException(int invalidGeneration) {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();

        // when & then
        assertThatThrownBy(() ->
                Review.create(content, author, invalidGeneration, Part.SERVER, category, subjects, url)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기수는 1 이상이어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 34, 50, 100})
    @DisplayName("유효한 기수로 생성 성공")
    void createWithValidGeneration(int validGeneration) {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();

        // when
        Review review = Review.create(content, author, validGeneration, Part.SERVER, category, subjects, url);

        // then
        assertThat(review).isNotNull();
        assertThat(review.getGeneration()).isEqualTo(validGeneration);
    }

    // === 파트 검증 테스트 ===

    @Test
    @DisplayName("null 파트로 생성 시 예외 발생")
    void createWithNullPartThrowsException() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();
        Part nullPart = null;

        // when & then
        assertThatThrownBy(() ->
                Review.create(content, author, 34, nullPart, category, subjects, url)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파트는 필수입니다.");
    }

    @Test
    @DisplayName("모든 파트로 리뷰 생성 가능")
    void createWithAllParts() {
        // given
        ReviewContent content = createValidContent();
        ReviewAuthor author = createValidAuthor();
        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());
        ReviewUrl url = createValidUrl();

        // when & then
        for (Part part : Part.values()) {
            Review review = Review.create(content, author, 34, part, category, subjects, url);
            assertThat(review.getPart()).isEqualTo(part);
        }
    }

    // === URL 중복 검증 테스트 ===

    @Test
    @DisplayName("URL이 중복되지 않으면 검증 성공")
    void validateUrlUniquenessWithNonDuplicate() {
        // given
        Review review = createValidReview();
        boolean isDuplicate = false;

        // when & then
        assertThatCode(() -> review.validateUrlUniqueness(isDuplicate))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("URL이 중복되면 예외 발생")
    void validateUrlUniquenessWithDuplicateThrowsException() {
        // given
        Review review = createValidReview();
        boolean isDuplicate = true;

        // when & then
        assertThatThrownBy(() -> review.validateUrlUniqueness(isDuplicate))
                .isInstanceOf(DuplicateReviewUrlException.class)
                .hasMessage("이미 등록된 활동후기입니다.");
    }

    // === 조회 편의 메서드 테스트 ===

    @Test
    @DisplayName("조회 편의 메서드가 올바른 값을 반환")
    void getterMethodsReturnCorrectValues() {
        // given
        ReviewContent content = new ReviewContent(
                "제목",
                "설명",
                "https://example.com/thumb.jpg",
                "Medium"
        );
        ReviewAuthor author = new ReviewAuthor("작성자", "https://example.com/profile.jpg");
        ReviewCategory category = new ReviewCategory(CategoryType.ACTIVITY);
        ReviewSubjects subjects = new ReviewSubjects(List.of("세미나", "프로젝트"));
        ReviewUrl url = new ReviewUrl("https://example.com/review");

        Review review = Review.create(content, author, 34, Part.SERVER, category, subjects, url);

        // when & then
        assertThat(review.getTitle()).isEqualTo("제목");
        assertThat(review.getDescription()).isEqualTo("설명");
        assertThat(review.getThumbnailUrl()).isEqualTo("https://example.com/thumb.jpg");
        assertThat(review.getPlatform()).isEqualTo("Medium");
        assertThat(review.getAuthorName()).isEqualTo("작성자");
        assertThat(review.getAuthorProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
        assertThat(review.getCategoryValue()).isEqualTo("전체 활동");
        assertThat(review.getSubjectValues()).containsExactly("세미나", "프로젝트");
        assertThat(review.getUrlValue()).isEqualTo("https://example.com/review");
        assertThat(review.getGeneration()).isEqualTo(34);
        assertThat(review.getPart()).isEqualTo(Part.SERVER);
    }

    @Test
    @DisplayName("썸네일 URL이 null이어도 조회 가능")
    void getThumbnailUrlWithNullValue() {
        // given
        ReviewContent content = new ReviewContent(
                "제목",
                "설명",
                null,  // 썸네일 없음
                "Medium"
        );
        Review review = Review.create(
                content,
                createValidAuthor(),
                34,
                Part.SERVER,
                new ReviewCategory(CategoryType.SEMINAR),
                new ReviewSubjects(List.of()),
                createValidUrl()
        );

        // when & then
        assertThat(review.getThumbnailUrl()).isNull();
    }

    @Test
    @DisplayName("프로필 이미지 URL이 null이어도 조회 가능")
    void getProfileImageUrlWithNullValue() {
        // given
        ReviewAuthor author = new ReviewAuthor("작성자", null);  // 프로필 이미지 없음
        Review review = Review.create(
                createValidContent(),
                author,
                34,
                Part.SERVER,
                new ReviewCategory(CategoryType.SEMINAR),
                new ReviewSubjects(List.of()),
                createValidUrl()
        );

        // when & then
        assertThat(review.getAuthorProfileImageUrl()).isNull();
    }

    // === 헬퍼 메서드 ===

    private ReviewContent createValidContent() {
        return new ReviewContent(
                "SOPT 활동 후기",
                "유익한 활동이었습니다.",
                "https://example.com/thumbnail.jpg",
                "Medium"
        );
    }

    private ReviewAuthor createValidAuthor() {
        return new ReviewAuthor("홍길동", "https://example.com/profile.jpg");
    }

    private ReviewUrl createValidUrl() {
        return new ReviewUrl("https://medium.com/@sopt/review-" + System.nanoTime());
    }

    private Review createValidReview() {
        return Review.create(
                createValidContent(),
                createValidAuthor(),
                34,
                Part.SERVER,
                new ReviewCategory(CategoryType.SEMINAR),
                new ReviewSubjects(List.of()),
                createValidUrl()
        );
    }
}