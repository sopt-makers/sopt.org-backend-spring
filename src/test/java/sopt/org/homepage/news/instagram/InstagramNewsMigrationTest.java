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

class InstagramNewsMigrationTest extends InstagramNewsIntegrationTestBase {
    @Test
    @DisplayName("기존 수동 뉴스 게시일 보정 마이그레이션은 인스타 게시일을 변경하지 않음")
    void backfillsLegacyManualNewsPublicationTime() throws Exception {
        News manual = news.saveAndFlush(News.builder().title("기존 수동 뉴스").link("https://example.com").image("image.jpg").build());
        Instant original = Instant.parse("2026-01-01T00:00:00Z");
        News imported = news.saveAndFlush(News.fromInstagramPost("수집 뉴스", "https://www.instagram.com/p/post1/", "image.jpg", "1", original));
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.prepareStatement("update main_news set published_at = null, created_at = timestamp '2025-12-31 15:00:00' where id = ?")) {
                statement.setInt(1, manual.getId());
                statement.executeUpdate();
            }
            org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(connection,
                    new org.springframework.core.io.ClassPathResource("db/migration/V12__backfill_manual_news_published_at.sql"));
        }
        assertThat(news.findById(manual.getId()).orElseThrow().getPublishedAt()).isEqualTo(Instant.parse("2025-12-31T15:00:00Z"));
        assertThat(news.findById(imported.getId()).orElseThrow().getPublishedAt()).isEqualTo(original);
    }

    @Test
    @DisplayName("✅ V13 마이그레이션을 실제 PostgreSQL에서 실행")
    void appliesReviewMigrations() throws Exception {
        try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            statement.execute("create schema pr297_migration_test");
            try {
                statement.execute("set search_path to pr297_migration_test");
                statement.execute("create table main_news (id integer, published_at timestamptz, created_at timestamp)");
                statement.execute("insert into main_news values (1, null, now())");
                for (String resource : List.of("V13__add_news_hidden.sql")) {
                    org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(connection,
                            new org.springframework.core.io.ClassPathResource("db/migration/" + resource));
                }
                try (var rows = statement.executeQuery("select hidden from main_news where id = 1")) {
                    assertThat(rows.next()).isTrue();
                    assertThat(rows.getBoolean(1)).isFalse();
                }
            } finally {
                statement.execute("set search_path to public");
                statement.execute("drop schema pr297_migration_test cascade");
            }
        }
    }

}
