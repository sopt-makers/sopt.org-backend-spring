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

class InstagramNewsPostProcessorTest extends IntegrationTestBase {
    private InstagramNewsScheduler scheduler;
    @Autowired NewsRepository news;
    @Autowired NewsService newsService;
    @Autowired InstagramNewsPostProcessor processor;
    @Autowired ObjectMapper mapper;
    @MockBean ApifyInstagramClient apify;
    @Autowired InstagramNewsFilter filter;
    @MockBean InstagramThumbnailStorage thumbnails;

    @BeforeEach
    void setUp() {
        scheduler = new InstagramNewsScheduler(apify, processor, new InstagramNewsConfig.Properties("test-token"));
        when(thumbnails.store(anyString(), anyString())).thenReturn("https://s3.example.com/news.jpg");
    }

    @AfterEach
    void cleanUp() {
        news.deleteAll();
    }

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
    @DisplayName("개별 게시물 실패가 다른 게시물 저장을 막지 않으며 다음 수집에서 재시도")
    void isolatesFailures() throws Exception {
        when(apify.start()).thenReturn("run");
        scheduler.collect();
        when(apify.run("run")).thenReturn(mapper.readTree("{\"status\":\"SUCCEEDED\",\"defaultDatasetId\":\"dataset\"}"));
        var first = post("1", "sopt_media_official", "2026-01-01T00:00:00Z");
        when(apify.items("dataset", 0)).thenReturn(List.of(first, post("2", "sopt_media_official", "2026-01-02T00:00:00Z")));
        when(thumbnails.store(eq("1"), anyString())).thenThrow(new IllegalStateException("download failed"));
        scheduler.poll();
        assertThat(news.findAll()).extracting(News::getInstagramPostId).containsExactly("2");
        assertThat(news.existsByInstagramPostId("1")).isFalse();
        scheduler.poll();
        verify(apify, times(1)).run("run");
        when(thumbnails.store(eq("1"), anyString())).thenReturn("https://s3.example.com/retry.jpg");
        scheduler.collect();
        scheduler.poll();
        verify(apify, times(2)).start();
        assertThat(news.findAll()).extracting(News::getInstagramPostId).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    @DisplayName("원본 업로드 일시 최신순이며 관리자 일괄 배포 후에도 수집 뉴스 보존")
    void orderAndPreserve() {
        processor.process(post("2", "sopt_media_official", "2026-02-01T00:00:00Z"));
        processor.process(post("1", "sopt_media_official", "2026-01-01T00:00:00Z"));
        news.save(News.builder().title("수동").link("https://example.com").image("manual.jpg").build());
        newsService.bulkCreate(new BulkCreateNewsCommand(List.of()));
        assertThat(newsService.findAll()).extracting(News::getInstagramPostId).containsExactly("2", "1");
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
    @DisplayName("파트장 예능·파트 소개는 노출하고 미디어팀 인사말은 제외 사유로 보지 않음")
    void keepsPartContent() {
        for (String title : List.of("파트장 영통 팬사인회", "파트 소개", "파트티 자랑대회")) {
            assertThat(filter.shouldPublish("[" + title + "]\n안녕하세요, 미디어팀 나우유씨미입니다"))
                    .as(title).isTrue();
        }
    }

    @Test
    @DisplayName("본문의 참가 신청 언급만으로 활동 스케치를 제외하지 않음")
    void noticeChecksTitleOnly() {
        assertThat(filter.shouldPublish("[APPJAM 현장 스케치]\n참가 신청부터 마무리까지 함께한 이야기")).isTrue();
        assertThat(filter.shouldPublish("[APPJAM 참가 신청 안내]\n현장 스케치를 준비합니다")).isFalse();
    }

    @Test
    @DisplayName("photo·hot 등의 일부인 ot는 주제 키워드로 인정하지 않음")
    void otMatchesOnlyOutsideLatinWords() {
        for (String word : List.of("photo", "hot", "spot", "not")) {
            assertThat(filter.shouldPublish("[신규 서비스 소개]\n" + word)).as(word).isFalse();
        }
        assertThat(filter.shouldPublish("[OT 영상]\n함께한 첫 만남")).isTrue();
        assertThat(filter.shouldPublish("[첫 만남 현장 스케치]\nOT에서 만났어요")).isTrue();
        assertThat(filter.shouldPublish("[현장 스케치]\n세미나에서 만났어요")).isTrue();
    }

    @Test
    @DisplayName("null 게시물 페이지도 처리 위치를 진행하고 다음 페이지를 저장")
    void skipsNullItemsAndAdvancesPage() throws Exception {
        when(apify.start()).thenReturn("run");
        when(apify.run("run")).thenReturn(mapper.readTree("{\"status\":\"SUCCEEDED\",\"defaultDatasetId\":\"dataset\"}"));
        when(apify.items("dataset", 0)).thenReturn(java.util.Collections.nCopies(10, null));
        when(apify.items("dataset", 10)).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        scheduler.collect();
        scheduler.poll();
        scheduler.poll();
        scheduler.poll();
        verify(apify).items("dataset", 10);
        verify(apify, times(2)).run("run");
        assertThat(news.findAll()).hasSize(1);
        scheduler.collect();
        verify(apify, times(2)).start();
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

    private InstagramPost post(String id, String owner, String timestamp) {
        return new InstagramPost(id, owner, "APPJAM 활동 스케치", "https://www.instagram.com/p/post" + id + "/",
                Instant.parse(timestamp), "https://scontent.cdninstagram.com/cover.jpg", List.of());
    }
}
