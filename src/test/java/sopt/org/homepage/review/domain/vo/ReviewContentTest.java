package sopt.org.homepage.review.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.review.exception.InvalidReviewContentException;

import static org.assertj.core.api.Assertions.*;

/**
 * ReviewContent 단위 테스트
 */
@DisplayName("ReviewContent 단위 테스트")
class ReviewContentTest {

    @Test
    @DisplayName("유효한 컨텐츠로 생성 성공")
    void createWithValidContent() {
        // given
        String title = "SOPT 34기 활동 후기";
        String description = "정말 유익한 활동이었습니다.";
        String thumbnailUrl = "https://example.com/thumbnail.jpg";
        String platform = "Medium";

        // when
        ReviewContent content = new ReviewContent(title, description, thumbnailUrl, platform);

        // then
        assertThat(content.getTitle()).isEqualTo(title);
        assertThat(content.getDescription()).isEqualTo(description);
        assertThat(content.getThumbnailUrl()).isEqualTo(thumbnailUrl);
        assertThat(content.getPlatform()).isEqualTo(platform);
    }

    @Test
    @DisplayName("썸네일 URL이 null이어도 생성 성공")
    void createWithNullThumbnail() {
        // given
        String thumbnailUrl = null;

        // when
        ReviewContent content = new ReviewContent(
                "제목", "설명", thumbnailUrl, "플랫폼"
        );

        // then
        assertThat(content.getThumbnailUrl()).isNull();
    }

    @Test
    @DisplayName("null 제목으로 생성 시 예외 발생")
    void createWithNullTitle() {
        // when & then
        assertThatThrownBy(() -> new ReviewContent(null, "설명", "url", "플랫폼"))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("리뷰 제목은 필수입니다.");
    }

    @Test
    @DisplayName("빈 제목으로 생성 시 예외 발생")
    void createWithBlankTitle() {
        // when & then
        assertThatThrownBy(() -> new ReviewContent("   ", "설명", "url", "플랫폼"))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("리뷰 제목은 필수입니다.");
    }

    @Test
    @DisplayName("1000자를 초과하는 제목으로 생성 시 예외 발생")
    void createWithTooLongTitle() {
        // given
        String longTitle = "a".repeat(1001);

        // when & then
        assertThatThrownBy(() -> new ReviewContent(longTitle, "설명", "url", "플랫폼"))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("리뷰 제목은 1000자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("null 설명으로 생성 시 예외 발생")
    void createWithNullDescription() {
        // when & then
        assertThatThrownBy(() -> new ReviewContent("제목", null, "url", "플랫폼"))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("리뷰 설명은 필수입니다.");
    }

    @Test
    @DisplayName("2000자를 초과하는 설명으로 생성 시 예외 발생")
    void createWithTooLongDescription() {
        // given
        String longDescription = "a".repeat(2001);

        // when & then
        assertThatThrownBy(() -> new ReviewContent("제목", longDescription, "url", "플랫폼"))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("리뷰 설명은 2000자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("null 플랫폼으로 생성 시 예외 발생")
    void createWithNullPlatform() {
        // when & then
        assertThatThrownBy(() -> new ReviewContent("제목", "설명", "url", null))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("플랫폼 정보는 필수입니다.");
    }

    @Test
    @DisplayName("500자를 초과하는 썸네일 URL로 생성 시 예외 발생")
    void createWithTooLongThumbnailUrl() {
        // given
        String longUrl = "https://example.com/" + "a".repeat(500);

        // when & then
        assertThatThrownBy(() -> new ReviewContent("제목", "설명", longUrl, "플랫폼"))
                .isInstanceOf(InvalidReviewContentException.class)
                .hasMessage("썸네일 URL은 500자를 초과할 수 없습니다.");
    }
}