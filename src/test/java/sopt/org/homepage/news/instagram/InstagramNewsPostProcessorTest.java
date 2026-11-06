package sopt.org.homepage.news.instagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsRepository;
import sopt.org.homepage.news.NewsService;
import sopt.org.homepage.news.dto.BulkCreateNewsCommand;

class InstagramNewsPostProcessorTest extends InstagramNewsIntegrationTestBase {
    @Test
    @DisplayName("게시일의 KST 연도 경계 및 계정 검증, 중복 방지")
    void filtersAndDeduplicates() {
        processor.process(post("1", "sopt_media_official", "2025-12-31T15:00:00Z"));
        processor.process(post("1", "sopt_media_official", "2025-12-31T15:00:00Z"));
        processor.process(post("2", "sopt_media_official", "2025-12-31T14:59:59Z"));
        processor.process(post("3", "another_account", "2026-01-01T00:00:00Z"));
        assertThat(news.findAll()).hasSize(1);
        assertThat(news.findAll().get(0).getPublishedDate()).isEqualTo("2026.01.01");
    }

    @Test
    @DisplayName("제외 게시물은 저장하지 않으며 다음 수집에서 다시 필터링")
    void onlyStoresEligiblePosts() {
        var notice = new InstagramPost("1", "sopt_media_official", "[솝커톤 참가 신청 안내]",
                "https://www.instagram.com/p/post1/", Instant.parse("2026-01-01T00:00:00Z"), "cover.jpg", List.of());
        var unknown = new InstagramPost("2", "sopt_media_official", "👀",
                "https://www.instagram.com/p/post2/", Instant.parse("2026-01-02T00:00:00Z"), "cover.jpg", List.of());
        processor.process(notice);
        processor.process(unknown);
        processor.process(notice);
        assertThat(news.findAll()).isEmpty();
        verifyNoInteractions(thumbnails);
        // 제외됐던 게시물도 캡션이 노출 조건을 충족하면 다음 수집에서 저장한다.
        processor.process(post("1", "sopt_media_official", "2026-01-01T00:00:00Z"));
        assertThat(news.findAll()).extracting(News::getInstagramPostId).containsExactly("1");
    }

    @Test
    @DisplayName("여러 장 게시물은 첫 번째 이미지, 릴스는 displayUrl을 썸네일로 사용")
    void thumbnailSelection() {
        var child = new InstagramPost(null, null, null, null, null, "first.jpg", null);
        var carousel = new InstagramPost("1", null, null, null, null, "parent.jpg", List.of(child));
        assertThat(carousel.thumbnail()).isEqualTo("first.jpg");
        assertThat(post("2", "sopt_media_official", "2026-01-01T00:00:00Z").thumbnail()).endsWith("cover.jpg");
    }

    @Test
    @DisplayName("임원진 소개는 확정 제외하며 제목 원문과 이모지를 유지")
    void excludesExecutiveIntroduction() {
        String caption = "[39기 PLAY SOPT 임원진을 소개합니다 🪄]\n\n안녕하세요, 39기 미디어팀 나우유씨미입니다 👀";
        assertThat(filter.shouldPublish(caption)).isFalse();
        assertThat(filter.title(caption)).isEqualTo("39기 PLAY SOPT 임원진을 소개합니다 🪄");
        processor.process(new InstagramPost("99", "sopt_media_official", caption,
                "https://www.instagram.com/p/executives/", Instant.parse("2026-01-01T00:00:00Z"), "cover.jpg", List.of()));
        assertThat(news.findAll()).isEmpty();
        verifyNoInteractions(thumbnails);
    }

    @Test
    @DisplayName("자식 썸네일이 null·공백이거나 자식 자체가 null이면 부모 커버 사용")
    void fallsBackToParentThumbnail() {
        for (String image : java.util.Arrays.asList(null, "", "  ")) {
            var child = new InstagramPost(null, null, null, null, null, image, null);
            assertThat(new InstagramPost("1", null, null, null, null, "parent.jpg", List.of(child)).thumbnail())
                    .isEqualTo("parent.jpg");
        }
        assertThat(new InstagramPost("1", null, null, null, null, "parent.jpg", java.util.Arrays.asList((InstagramPost) null)).thumbnail())
                .isEqualTo("parent.jpg");
    }

}
