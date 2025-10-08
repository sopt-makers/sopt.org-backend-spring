package sopt.org.homepage.soptstory.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * SoptStoryUrl Value Object 단위 테스트
 *
 * Mock 없이 순수 객체만으로 테스트
 */
@DisplayName("SoptStoryUrl 단위 테스트")
class SoptStoryUrlTest {

    @Test
    @DisplayName("유효한 HTTP URL로 생성 성공")
    void createWithValidHttpUrl() {
        // given
        String validUrl = "http://example.com/article";

        // when
        SoptStoryUrl url = new SoptStoryUrl(validUrl);

        // then
        assertThat(url.getValue()).isEqualTo(validUrl);
    }

    @Test
    @DisplayName("유효한 HTTPS URL로 생성 성공")
    void createWithValidHttpsUrl() {
        // given
        String validUrl = "https://blog.sopt.org/2024/12/sopticle";

        // when
        SoptStoryUrl url = new SoptStoryUrl(validUrl);

        // then
        assertThat(url.getValue()).isEqualTo(validUrl);
    }

    @Test
    @DisplayName("포트 번호가 포함된 URL로 생성 성공")
    void createWithPort() {
        // given
        String validUrl = "https://localhost:8080/article";

        // when
        SoptStoryUrl url = new SoptStoryUrl(validUrl);

        // then
        assertThat(url.getValue()).isEqualTo(validUrl);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("URL이 null이거나 빈 문자열이면 예외 발생")
    void createWithNullOrEmpty(String invalidUrl) {
        // when & then
        assertThatThrownBy(() -> new SoptStoryUrl(invalidUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL은 필수입니다.");
    }

    @Test
    @DisplayName("URL이 공백만 있으면 예외 발생")
    void createWithBlank() {
        // given
        String blankUrl = "   ";

        // when & then
        assertThatThrownBy(() -> new SoptStoryUrl(blankUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL은 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-url",
            "ftp://example.com",
            "example.com",
            "//example.com",
            "http:/example.com"
    })
    @DisplayName("잘못된 URL 형식이면 예외 발생")
    void createWithInvalidFormat(String invalidUrl) {
        // when & then
        assertThatThrownBy(() -> new SoptStoryUrl(invalidUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 URL 형식이 아닙니다");
    }

    @Test
    @DisplayName("URL 길이가 500자를 초과하면 예외 발생")
    void createWithTooLongUrl() {
        // given
        String tooLongUrl = "https://example.com/" + "a".repeat(500);

        // when & then
        assertThatThrownBy(() -> new SoptStoryUrl(tooLongUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL은 500자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("동일한 URL을 가진 VO는 동등하다")
    void equalsSameUrl() {
        // given
        String url = "https://example.com/article";
        SoptStoryUrl url1 = new SoptStoryUrl(url);
        SoptStoryUrl url2 = new SoptStoryUrl(url);

        // when & then
        assertThat(url1).isEqualTo(url2);
        assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
    }

    @Test
    @DisplayName("다른 URL을 가진 VO는 동등하지 않다")
    void notEqualsDifferentUrl() {
        // given
        SoptStoryUrl url1 = new SoptStoryUrl("https://example.com/article1");
        SoptStoryUrl url2 = new SoptStoryUrl("https://example.com/article2");

        // when & then
        assertThat(url1).isNotEqualTo(url2);
    }

    @Test
    @DisplayName("toString()은 URL 값을 반환한다")
    void toStringReturnsValue() {
        // given
        String url = "https://example.com/article";
        SoptStoryUrl soptStoryUrl = new SoptStoryUrl(url);

        // when & then
        assertThat(soptStoryUrl.toString()).isEqualTo(url);
    }
}