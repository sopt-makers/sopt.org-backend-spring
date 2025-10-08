package sopt.org.homepage.soptstory.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

/**
 * SoptStoryContent Value Object 단위 테스트
 */
@DisplayName("SoptStoryContent 단위 테스트")
class SoptStoryContentTest {

    @Test
    @DisplayName("유효한 값으로 생성 성공")
    void createWithValidValues() {
        // given
        String title = "SOPT 34기 모집";
        String description = "IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.";
        String thumbnailUrl = "https://example.com/thumbnail.jpg";

        // when
        SoptStoryContent content = new SoptStoryContent(title, description, thumbnailUrl);

        // then
        assertThat(content.getTitle()).isEqualTo(title);
        assertThat(content.getDescription()).isEqualTo(description);
        assertThat(content.getThumbnailUrl()).isEqualTo(thumbnailUrl);
    }

    @Test
    @DisplayName("썸네일 URL이 null이어도 생성 성공 (nullable)")
    void createWithoutThumbnail() {
        // given
        String title = "SOPT 34기 모집";
        String description = "IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.";

        // when
        SoptStoryContent content = new SoptStoryContent(title, description, null);

        // then
        assertThat(content.getTitle()).isEqualTo(title);
        assertThat(content.getDescription()).isEqualTo(description);
        assertThat(content.getThumbnailUrl()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("제목이 null이거나 빈 문자열이면 예외 발생")
    void createWithNullOrEmptyTitle(String invalidTitle) {
        // when & then
        assertThatThrownBy(() -> new SoptStoryContent(
                invalidTitle,
                "설명",
                "https://example.com/thumbnail.jpg"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수입니다.");
    }

    @Test
    @DisplayName("제목이 공백만 있으면 예외 발생")
    void createWithBlankTitle() {
        // when & then
        assertThatThrownBy(() -> new SoptStoryContent(
                "   ",
                "설명",
                "https://example.com/thumbnail.jpg"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수입니다.");
    }

    @Test
    @DisplayName("제목이 100자를 초과하면 예외 발생")
    void createWithTooLongTitle() {
        // given
        String tooLongTitle = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> new SoptStoryContent(
                tooLongTitle,
                "설명",
                "https://example.com/thumbnail.jpg"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 100자를 초과할 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("설명이 null이거나 빈 문자열이면 예외 발생")
    void createWithNullOrEmptyDescription(String invalidDescription) {
        // when & then
        assertThatThrownBy(() -> new SoptStoryContent(
                "제목",
                invalidDescription,
                "https://example.com/thumbnail.jpg"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("설명은 필수입니다.");
    }

    @Test
    @DisplayName("설명이 600자를 초과하면 예외 발생")
    void createWithTooLongDescription() {
        // given
        String tooLongDescription = "a".repeat(601);

        // when & then
        assertThatThrownBy(() -> new SoptStoryContent(
                "제목",
                tooLongDescription,
                "https://example.com/thumbnail.jpg"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("설명은 600자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("썸네일 URL이 500자를 초과하면 예외 발생")
    void createWithTooLongThumbnailUrl() {
        // given
        String tooLongUrl = "https://example.com/" + "a".repeat(500);

        // when & then
        assertThatThrownBy(() -> new SoptStoryContent(
                "제목",
                "설명",
                tooLongUrl
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("썸네일 URL은 500자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("동일한 내용의 VO는 동등하다")
    void equalsSameContent() {
        // given
        SoptStoryContent content1 = new SoptStoryContent(
                "제목", "설명", "https://example.com/thumb.jpg"
        );
        SoptStoryContent content2 = new SoptStoryContent(
                "제목", "설명", "https://example.com/thumb.jpg"
        );

        // when & then
        assertThat(content1).isEqualTo(content2);
        assertThat(content1.hashCode()).isEqualTo(content2.hashCode());
    }

    @Test
    @DisplayName("다른 내용의 VO는 동등하지 않다")
    void notEqualsDifferentContent() {
        // given
        SoptStoryContent content1 = new SoptStoryContent(
                "제목1", "설명1", "https://example.com/thumb1.jpg"
        );
        SoptStoryContent content2 = new SoptStoryContent(
                "제목2", "설명2", "https://example.com/thumb2.jpg"
        );

        // when & then
        assertThat(content1).isNotEqualTo(content2);
    }
}