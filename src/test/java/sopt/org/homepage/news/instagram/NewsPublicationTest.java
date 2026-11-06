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

class NewsPublicationTest extends InstagramNewsIntegrationTestBase {
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
    @DisplayName("수동 뉴스도 등록 시각으로 정렬하며 인스타 원본 게시일은 보존")
    void manualNewsHasPublicationTime() {
        Instant original = Instant.parse("2026-01-01T00:00:00Z");
        processor.process(post("1", "sopt_media_official", original.toString()));
        News manual = news.saveAndFlush(News.builder().title("수동 최신소식").link("https://example.com").image("manual.jpg").build());
        assertThat(manual.getPublishedAt()).isNotNull();
        assertThat(manual.getPublishedDate()).matches("\\d{4}\\.\\d{2}\\.\\d{2}");
        assertThat(newsService.findAll().get(0).getId()).isEqualTo(manual.getId());
        assertThat(news.findAll().stream().filter(n -> "1".equals(n.getInstagramPostId())).findFirst().orElseThrow().getPublishedAt()).isEqualTo(original);
    }

    @Test
    @DisplayName("관리자 일괄 저장에서 끝 슬래시 유무가 달라도 동일 인스타 링크 중복 방지")
    void bulkCreateDeduplicatesBothSlashVariants() {
        news.saveAndFlush(News.fromInstagramPost("수집1", "https://www.instagram.com/p/post1/", "one.jpg", "1", Instant.parse("2026-01-01T00:00:00Z")));
        news.saveAndFlush(News.fromInstagramPost("수집2", "https://www.instagram.com/p/post2", "two.jpg", "2", Instant.parse("2026-01-02T00:00:00Z")));
        newsService.bulkCreate(new BulkCreateNewsCommand(List.of(
                new BulkCreateNewsCommand.NewsData("중복1", "https://www.instagram.com/p/post1", "one.jpg"),
                new BulkCreateNewsCommand.NewsData("중복2", "https://www.instagram.com/p/post2/", "two.jpg"))));
        assertThat(news.findAll()).extracting(News::getInstagramPostId).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    @DisplayName("🔍 홈페이지는 최신 뉴스 최대 4개만 조회하고 관리자 조회는 전체 유지")
    void homepageLimitsNewsAtQueryTime() {
        org.springframework.test.util.ReflectionTestUtils.setField(newsService, "newsDisplayLimit", 4);
        assertThat(newsService.findLatestForHomepage()).isEmpty();
        for (int day : List.of(2, 1)) {
            news.saveAndFlush(News.fromInstagramPost("뉴스" + day, "https://www.instagram.com/p/post" + day + "/",
                    "image.jpg", String.valueOf(day), Instant.parse("2026-01-0" + day + "T00:00:00Z")));
        }
        assertThat(newsService.findLatestForHomepage()).extracting(News::getInstagramPostId).containsExactly("2", "1");
        for (int day : List.of(6, 3, 5, 4)) {
            news.saveAndFlush(News.fromInstagramPost("뉴스" + day, "https://www.instagram.com/p/post" + day + "/",
                    "image.jpg", String.valueOf(day), Instant.parse("2026-01-0" + day + "T00:00:00Z")));
        }
        assertThat(newsService.findLatestForHomepage()).extracting(News::getInstagramPostId).containsExactly("6", "5", "4", "3");
        assertThat(newsService.findAll()).hasSize(6);
        try {
            org.springframework.test.util.ReflectionTestUtils.setField(newsService, "newsDisplayLimit", 2);
            assertThat(newsService.findLatestForHomepage()).extracting(News::getInstagramPostId).containsExactly("6", "5");
        } finally {
            org.springframework.test.util.ReflectionTestUtils.setField(newsService, "newsDisplayLimit", 0);
        }
    }

    @Test
    @DisplayName("✅ 반복 배포해도 수동 뉴스의 ID와 게시일 보존")
    void preservesManualPublicationOnDeploy() {
        News manual = news.saveAndFlush(News.builder().title("수동").link("https://example.com").image("old.jpg").build());
        Instant published = manual.getPublishedAt();
        var command = new BulkCreateNewsCommand(List.of(new BulkCreateNewsCommand.NewsData("수정", manual.getLink(), "new.jpg")));
        newsService.bulkCreate(command);
        newsService.bulkCreate(command);
        News result = news.findById(manual.getId()).orElseThrow();
        assertThat(result.getPublishedAt()).isEqualTo(published);
        assertThat(result.getTitle()).isEqualTo("수정");
    }

    @Test
    @DisplayName("✅ 삭제한 인스타 뉴스는 재수집되지 않고 조회에서 숨김")
    void hidesImportedNewsPermanently() {
        var original = post("1", "sopt_media_official", "2026-01-01T00:00:00Z");
        processor.process(original);
        var request = new sopt.org.homepage.news.dto.DeleteAdminNewsRequestDto(news.findAll().get(0).getId());
        newsService.deleteMainNews(request);
        processor.process(original);
        assertThat(newsService.findAll()).isEmpty();
        assertThat(newsService.findLatestForHomepage()).isEmpty();
        assertThat(news.count()).isEqualTo(1);
    }

}
