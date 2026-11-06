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

class InstagramNewsCollectionTest extends InstagramNewsIntegrationTestBase {
    @Test
    @DisplayName("개별 게시물 실패가 다른 게시물 저장을 막지 않으며 다음 수집에서 재시도")
    void isolatesFailures() throws Exception {
        when(apify.start(any(java.time.Instant.class))).thenReturn("run");
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
        verify(apify, times(2)).start(any(java.time.Instant.class));
        assertThat(news.findAll()).extracting(News::getInstagramPostId).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    @DisplayName("null 게시물 페이지도 처리 위치를 진행하고 다음 페이지를 저장")
    void skipsNullItemsAndAdvancesPage() throws Exception {
        when(apify.start(any(java.time.Instant.class))).thenReturn("run");
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
        verify(apify, times(2)).start(any(java.time.Instant.class));
    }

    @Test
    @DisplayName("✅ 연속 폴링 실패 후 새 수집을 시작할 수 있음")
    void recoversAfterPollingFailures() {
        when(apify.start(any(java.time.Instant.class))).thenReturn("run", "next");
        when(apify.run("run")).thenThrow(new IllegalStateException("expired"));
        scheduler.startCollection();
        for (int i = 0; i < 10; i++) scheduler.poll();
        assertThat(scheduler.startCollection()).isEqualTo("next");
    }

    @Test
    @DisplayName("✅ 스케줄러를 다시 생성해도 DB 실행 ID와 다음 페이지를 이어받음")
    void resumesAfterRestart() throws Exception {
        when(apify.start(any(Instant.class))).thenReturn("run");
        when(apify.run("run")).thenReturn(mapper.readTree("{\"status\":\"SUCCEEDED\",\"defaultDatasetId\":\"dataset\"}"));
        when(apify.items("dataset", 0)).thenReturn(java.util.Collections.nCopies(10, null));
        when(apify.items("dataset", 10)).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        scheduler.startCollection();
        scheduler.poll();
        var restarted = new InstagramNewsScheduler(apify, processor, new InstagramNewsConfig.Properties("test-token"), collectionStore);
        assertThat(restarted.startCollection()).isEqualTo("run");
        restarted.poll();
        verify(apify, times(1)).start(any(Instant.class));
        verify(apify).items("dataset", 10);
        assertThat(news.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("✅ 다른 작업의 lease 중에는 중복 폴링하지 않고 관리자 요청은 기존 ID 반환")
    void preventsConcurrentPolling() {
        when(apify.start(any(Instant.class))).thenReturn("run");
        scheduler.startCollection();
        assertThat(collectionStore.claim("another-worker", false)).isTrue();
        scheduler.poll();
        assertThat(scheduler.startCollection()).isEqualTo("run");
        verify(apify, never()).run(anyString());
        collectionStore.release("another-worker");
    }

    @Test
    @DisplayName("✅ 만료된 lease는 재시작 후 다른 작업이 획득")
    void reclaimsExpiredLease() {
        when(apify.start(any(Instant.class))).thenReturn("run");
        scheduler.startCollection();
        assertThat(collectionStore.claim("crashed-worker", false)).isTrue();
        new org.springframework.jdbc.core.JdbcTemplate(dataSource).update(
                "update instagram_collection_state set lease_until = ? where id = 1", java.sql.Timestamp.from(Instant.now().minusSeconds(1)));
        assertThat(collectionStore.claim("replacement", false)).isTrue();
        assertThat(collectionStore.renew("crashed-worker")).isFalse();
        collectionStore.release("replacement");
    }

    @Test
    @DisplayName("✅ 성공한 전체 수집만 기준 시각을 진행하고 실패한 게시물은 같은 범위로 재시도")
    void advancesCollectionWindowOnlyAfterSuccess() throws Exception {
        when(apify.start(any(Instant.class))).thenReturn("run", "next");
        when(apify.run("run")).thenReturn(mapper.readTree("{\"status\":\"SUCCEEDED\",\"defaultDatasetId\":\"dataset\"}"));
        when(apify.items("dataset", 0)).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        when(thumbnails.store(eq("1"), anyString())).thenThrow(new IllegalStateException("failed"));
        scheduler.startCollection();
        scheduler.poll();
        assertThat(collectionStore.read().getCheckpoint()).isNull();
        when(thumbnails.store(eq("1"), anyString())).thenReturn("saved.jpg");
        scheduler.startCollection();
        when(apify.run("next")).thenReturn(mapper.readTree("{\"status\":\"SUCCEEDED\",\"defaultDatasetId\":\"dataset\"}"));
        scheduler.poll();
        Instant checkpoint = collectionStore.read().getCheckpoint();
        assertThat(checkpoint).isNotNull();
        var properties = new InstagramNewsConfig.Properties("token");
        assertThat(properties.collectionSince(checkpoint)).isEqualTo(checkpoint.minus(java.time.Duration.ofDays(7)));
    }

    @Test
    @DisplayName("✅ 중단된 실행의 부분 결과를 저장하되 수집 기준 시각은 유지")
    void preservesPartialResults() throws Exception {
        when(apify.start(any(Instant.class))).thenReturn("run");
        when(apify.run("run")).thenReturn(mapper.readTree("{\"status\":\"TIMED-OUT\",\"defaultDatasetId\":\"dataset\"}"));
        when(apify.items("dataset", 0)).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        scheduler.startCollection();
        scheduler.poll();
        assertThat(news.count()).isEqualTo(1);
        assertThat(collectionStore.read().getRunId()).isNull();
        assertThat(collectionStore.read().getCheckpoint()).isNull();
    }

    @Test
    @DisplayName("✅ 관리자 수집 API는 인증 전 수집을 시작하지 않고 비활성화 시 503")
    void protectsCollectionApi() throws Exception {
        var context = (org.springframework.web.context.WebApplicationContext) applicationContext;
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v2/admin/news/instagram/collect").contextPath("/v2"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
        String secret = context.getEnvironment().getProperty("jwt.admin");
        String token = io.jsonwebtoken.Jwts.builder().subject("1").signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8))).compact();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v2/admin/news/instagram/collect").contextPath("/v2")
                .header("Authorization", token))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isServiceUnavailable());
        verifyNoInteractions(apify);
    }

    @Test
    @DisplayName("✅ 수집 API는 인증 후 202와 실행 ID를 반환")
    void acceptsAuthenticatedCollection() throws Exception {
        var factory = new org.springframework.beans.factory.support.StaticListableBeanFactory();
        factory.addBean("collector", scheduler);
        var controller = new sopt.org.homepage.news.NewsController(newsService,
                factory.getBeanProvider(InstagramNewsScheduler.class));
        var context = (org.springframework.web.context.WebApplicationContext) applicationContext;
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller)
                .addFilters(context.getBean(sopt.org.homepage.global.common.filter.JwtExceptionFilter.class),
                        context.getBean(sopt.org.homepage.global.common.filter.JwtAuthenticationFilter.class)).build();
        String secret = context.getEnvironment().getProperty("jwt.admin");
        String token = io.jsonwebtoken.Jwts.builder().subject("1").signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8))).compact();
        when(apify.start(any(Instant.class))).thenReturn("run");
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v2/admin/news/instagram/collect").contextPath("/v2")
                .header("Authorization", token))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isAccepted())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.runId").value("run"));
    }

    @Test
    @DisplayName("✅ 썸네일 외부 I/O는 DB 트랜잭션 밖에서 실행")
    void downloadsOutsideTransaction() {
        when(thumbnails.store(anyString(), anyString())).thenAnswer(invocation -> {
            assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
            return "saved.jpg";
        });
        processor.process(post("1", "sopt_media_official", "2026-01-01T00:00:00Z"));
        assertThat(news.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("✅ 폴링 성공 시 연속 오류 횟수를 초기화")
    void resetsConsecutiveFailures() throws Exception {
        when(apify.start(any(Instant.class))).thenReturn("run");
        scheduler.startCollection();
        when(apify.run("run")).thenThrow(new IllegalStateException("temporary"));
        for (int i = 0; i < 9; i++) scheduler.poll();
        doReturn(mapper.readTree("{\"status\":\"RUNNING\"}")).when(apify).run("run");
        scheduler.poll();
        assertThat(collectionStore.read().getFailures()).isZero();
        when(apify.run("run")).thenThrow(new IllegalStateException("temporary"));
        scheduler.poll();
        assertThat(collectionStore.read().getRunId()).isEqualTo("run");
    }

}
