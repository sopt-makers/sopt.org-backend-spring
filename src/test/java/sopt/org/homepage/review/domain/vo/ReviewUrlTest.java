package sopt.org.homepage.review.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.review.exception.InvalidReviewUrlException;

import static org.assertj.core.api.Assertions.*;

/**
 * ReviewUrl 단위 테스트
 */
@DisplayName("ReviewUrl 단위 테스트")
class ReviewUrlTest {

    @Test
    @DisplayName("유효한 http URL로 생성 성공")
    void createWithValidHttpUrl() {
        // given
        String url = "http://example.com/review";

        // when
        ReviewUrl reviewUrl = new ReviewUrl(url);

        // then
        assertThat(reviewUrl.getValue()).isEqualTo(url);
    }

    @Test
    @DisplayName("유효한 https URL로 생성 성공")
    void createWithValidHttpsUrl() {
        // given
        String url = "https://medium.com/@sopt/review-article";

        // when
        ReviewUrl reviewUrl = new ReviewUrl(url);

        // then
        assertThat(reviewUrl.getValue()).isEqualTo(url);
    }

    @Test
    @DisplayName("null URL로 생성 시 예외 발생")
    void createWithNullUrl() {
        // when & then
        assertThatThrownBy(() -> new ReviewUrl(null))
                .isInstanceOf(InvalidReviewUrlException.class)
                .hasMessage("리뷰 URL은 필수입니다.");
    }

    @Test
    @DisplayName("빈 URL로 생성 시 예외 발생")
    void createWithBlankUrl() {
        // when & then
        assertThatThrownBy(() -> new ReviewUrl("   "))
                .isInstanceOf(InvalidReviewUrlException.class)
                .hasMessage("리뷰 URL은 필수입니다.");
    }

    @Test
    @DisplayName("500자를 초과하는 URL로 생성 시 예외 발생")
    void createWithTooLongUrl() {
        // given
        String longUrl = "https://example.com/" + "a".repeat(500);

        // when & then
        assertThatThrownBy(() -> new ReviewUrl(longUrl))
                .isInstanceOf(InvalidReviewUrlException.class)
                .hasMessage("리뷰 URL은 500자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("http 또는 https로 시작하지 않는 URL로 생성 시 예외 발생")
    void createWithInvalidUrlFormat() {
        // given
        String invalidUrl = "ftp://example.com";

        // when & then
        assertThatThrownBy(() -> new ReviewUrl(invalidUrl))
                .isInstanceOf(InvalidReviewUrlException.class)
                .hasMessage("유효한 URL 형식이 아닙니다. (http:// 또는 https://로 시작해야 합니다)");
    }

    @Test
    @DisplayName("프로토콜이 없는 URL로 생성 시 예외 발생")
    void createWithUrlWithoutProtocol() {
        // given
        String invalidUrl = "example.com";

        // when & then
        assertThatThrownBy(() -> new ReviewUrl(invalidUrl))
                .isInstanceOf(InvalidReviewUrlException.class)
                .hasMessage("유효한 URL 형식이 아닙니다. (http:// 또는 https://로 시작해야 합니다)");
    }
}