package sopt.org.homepage.news.instagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsController;

class InstagramNewsCollectionTest extends InstagramNewsIntegrationTestBase {
    @Test
    @DisplayName("✅ 재생성한 Collector도 Apify 마지막 실행 결과 전체를 저장하며 중복 방지")
    void resumesWithoutLocalState() throws Exception {
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        var posts = new ArrayList<InstagramPost>();
        for (int i = 1; i <= 25; i++) posts.add(post(String.valueOf(i), "sopt_media_official", "2026-01-01T00:00:00Z"));
        when(apify.items("dataset")).thenReturn(posts);
        collector.importLatest();
        var restarted = new InstagramNewsCollector(apify, processor, new InstagramNewsConfig.Properties("test-token", "test-task"), news);
        restarted.importLatest();
        assertThat(news.count()).isEqualTo(25);
        verify(apify, times(2)).items("dataset");
        verify(thumbnails, times(25)).store(anyString(), anyString());
    }

    @Test
    @DisplayName("✅ 개별 저장 실패는 다음 결과 조회에서 재시도하며 다른 게시물을 막지 않음")
    void retriesFailedPosts() throws Exception {
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z"),
                post("2", "sopt_media_official", "2026-01-02T00:00:00Z")));
        when(thumbnails.store(eq("1"), anyString())).thenThrow(new IllegalStateException("download failed"));
        collector.importLatest();
        assertThat(news.findAll()).extracting(News::getInstagramPostId).containsExactly("2");
        when(thumbnails.store(eq("1"), anyString())).thenReturn("saved.jpg");
        collector.importLatest();
        assertThat(news.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("✅ 조회 오류가 반복돼도 다음 호출은 새로운 Apify 상태를 읽고 복구")
    void recoversAfterRepeatedErrors() throws Exception {
        when(apify.lastRun()).thenThrow(new IllegalStateException("temporary"));
        for (int i = 0; i < 12; i++) collector.importLatest();
        doReturn(run("SUCCEEDED")).when(apify).lastRun();
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        collector.importLatest();
        assertThat(news.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("✅ 실행 중이면 원격 실행 ID를 반환하고 완료되기 전에는 결과를 읽지 않음")
    void reusesRemoteActiveRun() throws Exception {
        when(apify.lastRun()).thenReturn(run("RUNNING"));
        assertThat(collector.startCollection()).isEqualTo("run");
        collector.importLatest();
        verify(apify, never()).start(any());
        verify(apify, never()).items(anyString());
    }

    @Test
    @DisplayName("✅ 실패·중단·시간 초과의 부분 결과도 저장")
    void preservesPartialResults() throws Exception {
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        for (String status : List.of("FAILED", "ABORTED", "TIMED-OUT")) {
            when(apify.lastRun()).thenReturn(run(status));
            collector.importLatest();
        }
        assertThat(news.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("✅ 이전 데이터셋에 오래된 미저장 게시물이 있으면 다음 수집 기간에 포함")
    void preservesFailedPostWindow() throws Exception {
        news.saveAndFlush(News.fromInstagramPost("최신", "https://www.instagram.com/p/latest/", "image.jpg", "2",
                Instant.parse("2026-03-01T00:00:00Z")));
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        when(apify.start(any())).thenReturn("next");
        assertThat(collector.startCollection()).isEqualTo("next");
        verify(apify).start(Instant.parse("2025-12-31T23:59:59Z"));
        verifyNoInteractions(thumbnails);
    }

    @Test
    @DisplayName("✅ 인스타 게시일 기준 증분 수집에 수동 뉴스 게시일은 영향을 주지 않음")
    void usesInstagramPublicationWindow() {
        Instant latest = Instant.parse("2026-03-01T00:00:00Z");
        news.saveAndFlush(News.fromInstagramPost("최신", "https://www.instagram.com/p/latest/", "image.jpg", "2", latest));
        news.saveAndFlush(News.builder().title("수동").link("https://example.com").image("image.jpg").build());
        when(apify.start(any())).thenReturn("next");
        collector.startCollection();
        verify(apify).start(latest.minus(java.time.Duration.ofDays(7)));
    }

    @Test
    @DisplayName("✅ 첫 수집은 설정된 하한부터 시작")
    void startsFirstCollection() {
        when(apify.start(any())).thenReturn("run");
        assertThat(collector.startCollection()).isEqualTo("run");
        verify(apify).start(Instant.parse("2025-12-31T15:00:00Z"));
    }

    @Test
    @DisplayName("✅ null 게시물은 건너뛰며 외부 I/O는 트랜잭션 밖에서 수행")
    void skipsNullAndDownloadsOutsideTransaction() throws Exception {
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        when(apify.items("dataset")).thenReturn(java.util.Arrays.asList(null, post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        when(thumbnails.store(anyString(), anyString())).thenAnswer(invocation -> {
            assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
            return "saved.jpg";
        });
        collector.importLatest();
        assertThat(news.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("✅ 관리자 수집 API는 미인증 401, 비활성화 503 반환")
    void protectsCollectionApi() throws Exception {
        var context = (org.springframework.web.context.WebApplicationContext) applicationContext;
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
        mvc.perform(MockMvcRequestBuilders.post("/v2/admin/news/instagram/collect").contextPath("/v2"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.post("/v2/admin/news/instagram/collect").contextPath("/v2")
                .header("Authorization", adminToken()))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable());
        verifyNoInteractions(apify);
    }

    @Test
    @DisplayName("✅ 수집 API는 인증 후 202와 실행 ID를 반환")
    void acceptsAuthenticatedCollection() throws Exception {
        var factory = new org.springframework.beans.factory.support.StaticListableBeanFactory();
        factory.addBean("collector", collector);
        var controller = new NewsController(newsService, factory.getBeanProvider(InstagramNewsCollector.class));
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .addFilters(applicationContext.getBean(sopt.org.homepage.global.common.filter.JwtExceptionFilter.class),
                        applicationContext.getBean(sopt.org.homepage.global.common.filter.JwtAuthenticationFilter.class)).build();
        when(apify.start(any())).thenReturn("run");
        mvc.perform(MockMvcRequestBuilders.post("/v2/admin/news/instagram/collect").contextPath("/v2")
                .header("Authorization", adminToken()))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.runId").value("run"));
    }

    @Test
    @DisplayName("✅ 이전 완료 결과가 손상되어도 다음 수집은 하한부터 다시 시작")
    void restartsAfterMalformedCompletedRun() throws Exception {
        when(apify.lastRun()).thenReturn(mapper.readTree("{\"id\":\"old\",\"status\":\"SUCCEEDED\"}"));
        when(apify.start(any())).thenReturn("new");
        assertThat(collector.startCollection()).isEqualTo("new");
        verify(apify).start(new InstagramNewsConfig.Properties("token", "test-task").since());
    }

    @Test
    @DisplayName("✅ 동시 시작 요청은 중복 실행을 만들지 않고 409 반환")
    void preventsConcurrentStarts() throws Exception {
        var entered = new java.util.concurrent.CountDownLatch(1);
        var release = new java.util.concurrent.CountDownLatch(1);
        when(apify.start(any())).thenAnswer(invocation -> {
            entered.countDown();
            if (!release.await(5, java.util.concurrent.TimeUnit.SECONDS)) throw new IllegalStateException("test timed out");
            return "run";
        });
        var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        try {
            var first = executor.submit(collector::startCollection);
            assertThat(entered.await(5, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
            org.assertj.core.api.Assertions.assertThatThrownBy(collector::startCollection)
                    .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                    .satisfies(error -> assertThat(((org.springframework.web.server.ResponseStatusException) error).getStatusCode().value()).isEqualTo(409));
            release.countDown();
            assertThat(first.get(5, java.util.concurrent.TimeUnit.SECONDS)).isEqualTo("run");
            verify(apify, times(1)).start(any());
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    @DisplayName("✅ 오래된 게시물 재시도 중 연속 실패해도 원격 INPUT의 수집 하한 유지")
    void preservesRetryWindowAcrossConsecutiveFailedRuns() throws Exception {
        news.saveAndFlush(News.fromInstagramPost("최신", "https://www.instagram.com/p/latest/", "image.jpg", "2",
                Instant.parse("2026-03-01T00:00:00Z")));
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        when(apify.start(any())).thenReturn("retry1", "retry2", "retry3");
        collector.startCollection();
        Instant retrySince = Instant.parse("2025-12-31T23:59:59Z");
        when(apify.input("retry-input")).thenReturn(mapper.readTree(
                "{\"onlyPostsNewerThan\":\"" + retrySince + "\",\"resultsLimit\":1000}"));
        when(apify.lastRun()).thenReturn(mapper.readTree(
                "{\"id\":\"retry1\",\"status\":\"FAILED\",\"defaultKeyValueStoreId\":\"retry-input\"}"));
        collector.startCollection();
        when(apify.lastRun()).thenReturn(mapper.readTree(
                "{\"id\":\"retry2\",\"status\":\"TIMED-OUT\",\"defaultKeyValueStoreId\":\"retry-input\"}"));
        collector.startCollection();
        verify(apify, times(3)).start(retrySince);
    }

    @Test
    @DisplayName("✅ 결과 한도 도달 후 동일 유료 수집을 반복하지 않고 409 반환")
    void blocksRepeatedCappedCollection() throws Exception {
        var properties = new InstagramNewsConfig.Properties("token", false, "test-task",
                Instant.parse("2025-12-31T15:00:00Z"), 7, 2, List.of());
        var limited = new InstagramNewsCollector(apify, processor, properties, news);
        when(apify.input(anyString())).thenReturn(mapper.createObjectNode()
                .put("onlyPostsNewerThan", properties.since().toString()).put("resultsLimit", 2));
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z"),
                post("2", "sopt_media_official", "2026-01-02T00:00:00Z")));
        for (int i = 0; i < 2; i++) {
            org.assertj.core.api.Assertions.assertThatThrownBy(limited::startCollection)
                    .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                    .satisfies(error -> assertThat(((org.springframework.web.server.ResponseStatusException) error).getStatusCode().value()).isEqualTo(409));
        }
        verify(apify, never()).start(any());
        // 수집을 중단해도 이미 확보한 결과는 계속 저장한다.
        limited.importLatest();
        assertThat(news.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("✅ 한도를 올린 재시도는 이전 INPUT 기간을 유지하며 이후 정상 증분 수집")
    void retriesCappedWindowOnlyAfterIncreasingLimit() throws Exception {
        Instant originalSince = Instant.parse("2026-01-31T23:59:59Z");
        Instant latest = Instant.parse("2026-03-01T00:00:00Z");
        news.saveAndFlush(News.fromInstagramPost("최신", "https://www.instagram.com/p/latest/", "image.jpg", "9", latest));
        when(apify.lastRun()).thenReturn(mapper.readTree(
                "{\"id\":\"old\",\"status\":\"SUCCEEDED\",\"defaultDatasetId\":\"dataset\",\"defaultKeyValueStoreId\":\"input\"}"));
        when(apify.input("input")).thenReturn(mapper.readTree(
                "{\"onlyPostsNewerThan\":\"" + originalSince + "\",\"resultsLimit\":2}"));
        when(apify.items("dataset")).thenReturn(List.of(post("1", "sopt_media_official", "2026-02-01T00:00:00Z"),
                post("2", "sopt_media_official", "2026-02-02T00:00:00Z")));
        when(apify.start(any())).thenReturn("new");
        collector.startCollection();
        verify(apify).start(originalSince);
        // 한도에 닿지 않은 정상 완료 이후에는 최근 중복 구간만 다시 읽는다.
        collector.importLatest();
        when(apify.input("input")).thenReturn(mapper.readTree(
                "{\"onlyPostsNewerThan\":\"" + originalSince + "\",\"resultsLimit\":1000}"));
        collector.startCollection();
        verify(apify).start(latest.minus(java.time.Duration.ofDays(7)));
    }

    @Test
    @DisplayName("❌ 수집 활성화 시 환경별 Task ID 누락을 기동 설정 검증에서 거부")
    void requiresTaskWhenEnabled() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> new InstagramNewsConfig.Properties(
                "token", true, "", Instant.parse("2025-12-31T15:00:00Z"), 7, 1000, List.of()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("task-id");
    }

    @Test
    @DisplayName("✅ 이전 INPUT이 만료되면 데이터 누락 방지를 위해 설정 하한부터 재수집")
    void preservesWindowWhenInputExpired() throws Exception {
        news.saveAndFlush(News.fromInstagramPost("최신", "https://www.instagram.com/p/latest/", "image.jpg", "2",
                Instant.parse("2026-03-01T00:00:00Z")));
        when(apify.lastRun()).thenReturn(run("SUCCEEDED"));
        when(apify.input(anyString())).thenReturn(com.fasterxml.jackson.databind.node.MissingNode.getInstance());
        when(apify.items("dataset")).thenReturn(List.of());
        when(apify.start(any())).thenReturn("new");
        collector.startCollection();
        verify(apify).start(new InstagramNewsConfig.Properties("token", "test-task").since());
    }

    private String adminToken() {
        String secret = applicationContext.getEnvironment().getProperty("jwt.admin");
        return io.jsonwebtoken.Jwts.builder().subject("1").signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8))).compact();
    }

    private JsonNode run(String status) throws Exception {
        return mapper.readTree("{\"id\":\"run\",\"status\":\"" + status + "\",\"defaultDatasetId\":\"dataset\"}");
    }
}
