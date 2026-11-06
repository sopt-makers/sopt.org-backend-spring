package sopt.org.homepage.news.instagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.Instant;
import sopt.org.homepage.infrastructure.external.apify.ApifyInstagramClient;
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

abstract class InstagramNewsIntegrationTestBase extends IntegrationTestBase {
    InstagramNewsCollector collector;
    @Autowired org.springframework.context.ApplicationContext applicationContext;
    @Autowired NewsRepository news;
    @Autowired NewsService newsService;
    @Autowired InstagramNewsPostProcessor processor;
    @Autowired ObjectMapper mapper;
    @Autowired javax.sql.DataSource dataSource;
    @MockBean ApifyInstagramClient apify;
    @Autowired InstagramNewsFilter filter;
    @MockBean InstagramThumbnailStorage thumbnails;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(newsService, "newsDisplayLimit", 0);
        collector = new InstagramNewsCollector(apify, processor, new InstagramNewsConfig.Properties("test-token", "test-task"), news);
        when(apify.lastRun()).thenReturn(com.fasterxml.jackson.databind.node.MissingNode.getInstance());
        when(thumbnails.store(anyString(), anyString())).thenReturn("https://s3.example.com/news.jpg");
    }

    @AfterEach
    void cleanUp() {
        news.deleteAll();
    }

    InstagramPost post(String id, String owner, String timestamp) {
        return new InstagramPost(id, owner, "APPJAM 활동 스케치", "https://www.instagram.com/p/post" + id + "/",
                Instant.parse(timestamp), "https://scontent.cdninstagram.com/cover.jpg", List.of());
    }
}
