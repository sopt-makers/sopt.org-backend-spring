package sopt.org.homepage.soptstory.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.soptstory.domain.vo.LikeCount;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryContent;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryUrl;

import static org.assertj.core.api.Assertions.*;

/**
 * SoptStory Entity 단위 테스트
 *
 * Mock 없이 도메인 로직만 테스트
 */
@DisplayName("SoptStory Entity 단위 테스트")
class SoptStoryTest {

    @Test
    @DisplayName("유효한 값으로 SoptStory 생성 성공")
    void createSoptStory() {
        // given
        SoptStoryContent content = new SoptStoryContent(
                "SOPT 34기 모집",
                "IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.",
                "https://example.com/thumbnail.jpg"
        );
        SoptStoryUrl url = new SoptStoryUrl("https://blog.sopt.org/2024/recruit");

        // when
        SoptStory soptStory = SoptStory.create(content, url);

        // then
        assertThat(soptStory).isNotNull();
        assertThat(soptStory.getContent()).isEqualTo(content);
        assertThat(soptStory.getUrl()).isEqualTo(url);
        assertThat(soptStory.getLikeCount()).isEqualTo(LikeCount.initial());
        assertThat(soptStory.getLikeCountValue()).isZero();
    }

    @Test
    @DisplayName("생성 시 초기 좋아요 개수는 0이다")
    void createWithZeroLikes() {
        // given
        SoptStoryContent content = new SoptStoryContent("제목", "설명", null);
        SoptStoryUrl url = new SoptStoryUrl("https://example.com/article");

        // when
        SoptStory soptStory = SoptStory.create(content, url);

        // then
        assertThat(soptStory.getLikeCountValue()).isZero();
        assertThat(soptStory.getLikeCount().isZero()).isTrue();
    }

    @Test
    @DisplayName("좋아요 증가 성공")
    void incrementLike() {
        // given
        SoptStory soptStory = createDefaultSoptStory();

        // when
        soptStory.incrementLike();

        // then
        assertThat(soptStory.getLikeCountValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 번 좋아요 증가 성공")
    void incrementLikeMultipleTimes() {
        // given
        SoptStory soptStory = createDefaultSoptStory();

        // when
        soptStory.incrementLike();
        soptStory.incrementLike();
        soptStory.incrementLike();

        // then
        assertThat(soptStory.getLikeCountValue()).isEqualTo(3);
    }

    @Test
    @DisplayName("좋아요 증가는 메서드 체이닝 가능")
    void incrementLikeChaining() {
        // given
        SoptStory soptStory = createDefaultSoptStory();

        // when
        SoptStory result = soptStory.incrementLike().incrementLike();

        // then
        assertThat(result).isSameAs(soptStory);
        assertThat(soptStory.getLikeCountValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 감소 성공")
    void decrementLike() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        soptStory.incrementLike();
        soptStory.incrementLike();
        soptStory.incrementLike();

        // when
        soptStory.decrementLike();

        // then
        assertThat(soptStory.getLikeCountValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요가 0일 때 감소하면 예외 발생")
    void decrementLikeWhenZero() {
        // given
        SoptStory soptStory = createDefaultSoptStory();

        // when & then
        assertThatThrownBy(() -> soptStory.decrementLike())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("좋아요 개수는 음수가 될 수 없습니다.");
    }

    @Test
    @DisplayName("좋아요 감소는 메서드 체이닝 가능")
    void decrementLikeChaining() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        soptStory.incrementLike().incrementLike().incrementLike();

        // when
        SoptStory result = soptStory.decrementLike().decrementLike();

        // then
        assertThat(result).isSameAs(soptStory);
        assertThat(soptStory.getLikeCountValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 증가/감소 혼합 시나리오")
    void mixedLikeOperations() {
        // given
        SoptStory soptStory = createDefaultSoptStory();

        // when: 5번 증가, 2번 감소, 3번 증가
        soptStory.incrementLike()
                .incrementLike()
                .incrementLike()
                .incrementLike()
                .incrementLike()
                .decrementLike()
                .decrementLike()
                .incrementLike()
                .incrementLike()
                .incrementLike();

        // then: 5 - 2 + 3 = 6
        assertThat(soptStory.getLikeCountValue()).isEqualTo(6);
    }

    @Test
    @DisplayName("getUrlValue()는 URL 문자열을 반환한다")
    void getUrlValue() {
        // given
        String urlString = "https://example.com/article";
        SoptStoryUrl url = new SoptStoryUrl(urlString);
        SoptStory soptStory = SoptStory.create(
                new SoptStoryContent("제목", "설명", null),
                url
        );

        // when & then
        assertThat(soptStory.getUrlValue()).isEqualTo(urlString);
    }

    @Test
    @DisplayName("getTitle()은 제목을 반환한다")
    void getTitle() {
        // given
        String title = "SOPT 34기 모집";
        SoptStory soptStory = SoptStory.create(
                new SoptStoryContent(title, "설명", null),
                new SoptStoryUrl("https://example.com/article")
        );

        // when & then
        assertThat(soptStory.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("getDescription()은 설명을 반환한다")
    void getDescription() {
        // given
        String description = "IT 벤처 창업 동아리 SOPT";
        SoptStory soptStory = SoptStory.create(
                new SoptStoryContent("제목", description, null),
                new SoptStoryUrl("https://example.com/article")
        );

        // when & then
        assertThat(soptStory.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("getThumbnailUrl()은 썸네일 URL을 반환한다")
    void getThumbnailUrl() {
        // given
        String thumbnailUrl = "https://example.com/thumbnail.jpg";
        SoptStory soptStory = SoptStory.create(
                new SoptStoryContent("제목", "설명", thumbnailUrl),
                new SoptStoryUrl("https://example.com/article")
        );

        // when & then
        assertThat(soptStory.getThumbnailUrl()).isEqualTo(thumbnailUrl);
    }

    @Test
    @DisplayName("썸네일 URL이 없어도 조회 가능")
    void getThumbnailUrlWhenNull() {
        // given
        SoptStory soptStory = SoptStory.create(
                new SoptStoryContent("제목", "설명", null),
                new SoptStoryUrl("https://example.com/article")
        );

        // when & then
        assertThat(soptStory.getThumbnailUrl()).isNull();
    }

    // ===== Helper Methods =====

    private SoptStory createDefaultSoptStory() {
        SoptStoryContent content = new SoptStoryContent(
                "테스트 제목",
                "테스트 설명",
                "https://example.com/thumbnail.jpg"
        );
        SoptStoryUrl url = new SoptStoryUrl("https://example.com/test-article");
        return SoptStory.create(content, url);
    }
}