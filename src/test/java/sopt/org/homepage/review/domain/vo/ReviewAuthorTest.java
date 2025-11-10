package sopt.org.homepage.review.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.review.exception.InvalidReviewAuthorException;

import static org.assertj.core.api.Assertions.*;

/**
 * ReviewAuthor 단위 테스트
 */
@DisplayName("ReviewAuthor 단위 테스트")
class ReviewAuthorTest {

    @Test
    @DisplayName("유효한 작성자 정보로 생성 성공")
    void createWithValidAuthor() {
        // given
        String name = "홍길동";
        String profileImageUrl = "https://example.com/profile.jpg";

        // when
        ReviewAuthor author = new ReviewAuthor(name, profileImageUrl);

        // then
        assertThat(author.getName()).isEqualTo(name);
        assertThat(author.getProfileImageUrl()).isEqualTo(profileImageUrl);
    }

    @Test
    @DisplayName("프로필 이미지 URL이 null이어도 생성 성공")
    void createWithNullProfileImageUrl() {
        // given
        String profileImageUrl = null;

        // when
        ReviewAuthor author = new ReviewAuthor("홍길동", profileImageUrl);

        // then
        assertThat(author.getProfileImageUrl()).isNull();
    }

    @Test
    @DisplayName("null 작성자명으로 생성 시 예외 발생")
    void createWithNullName() {
        // when & then
        assertThatThrownBy(() -> new ReviewAuthor(null, "url"))
                .isInstanceOf(InvalidReviewAuthorException.class)
                .hasMessage("작성자명은 필수입니다.");
    }

    @Test
    @DisplayName("빈 작성자명으로 생성 시 예외 발생")
    void createWithBlankName() {
        // when & then
        assertThatThrownBy(() -> new ReviewAuthor("   ", "url"))
                .isInstanceOf(InvalidReviewAuthorException.class)
                .hasMessage("작성자명은 필수입니다.");
    }

    @Test
    @DisplayName("20자를 초과하는 작성자명으로 생성 시 예외 발생")
    void createWithTooLongName() {
        // given
        String longName = "a".repeat(21);

        // when & then
        assertThatThrownBy(() -> new ReviewAuthor(longName, "url"))
                .isInstanceOf(InvalidReviewAuthorException.class)
                .hasMessage("작성자명은 20자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("500자를 초과하는 프로필 이미지 URL로 생성 시 예외 발생")
    void createWithTooLongProfileImageUrl() {
        // given
        String longUrl = "https://example.com/" + "a".repeat(500);

        // when & then
        assertThatThrownBy(() -> new ReviewAuthor("홍길동", longUrl))
                .isInstanceOf(InvalidReviewAuthorException.class)
                .hasMessage("프로필 이미지 URL은 500자를 초과할 수 없습니다.");
    }
}