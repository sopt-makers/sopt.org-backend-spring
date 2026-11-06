package sopt.org.homepage.news.instagram;

import static org.assertj.core.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import feign.Feign;
import feign.Retryer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import sopt.org.homepage.infrastructure.external.apify.ApifyApi;
import sopt.org.homepage.infrastructure.external.apify.ApifyFeignConfig;
import sopt.org.homepage.infrastructure.external.apify.ApifyInstagramClient;

/** 실제 Feign 요청을 로컬 HTTP 서버로 보내 경로·헤더·응답 처리를 검증한다. */
class ApifyInstagramClientTest extends InstagramNewsIntegrationTestBase {
    private HttpServer server;
    private ApifyInstagramClient client;
    private volatile int responseStatus;
    private volatile String responseBody;
    private final AtomicReference<String> requestUri = new AtomicReference<>();
    private final AtomicReference<String> authorization = new AtomicReference<>();
    private final AtomicReference<String> requestBody = new AtomicReference<>();

    @BeforeEach
    void startServer() throws Exception {
        responseStatus = 200;
        responseBody = "{}";
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requestUri.set(exchange.getRequestURI().toString());
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseStatus, bytes.length);
            try (var output = exchange.getResponseBody()) { output.write(bytes); }
            exchange.close();
        });
        server.start();
        ApifyApi api = Feign.builder().contract(new SpringMvcContract())
                .options(new ApifyFeignConfig().apifyRequestOptions()).retryer(Retryer.NEVER_RETRY)
                .encoder((value, type, template) -> {
                    try { template.body(mapper.writeValueAsString(value)); }
                    catch (Exception e) { throw new IllegalStateException(e); }
                })
                .decoder((response, type) -> mapper.readValue(response.body().asInputStream(), mapper.constructType(type)))
                .target(ApifyApi.class, "http://127.0.0.1:" + server.getAddress().getPort());
        client = new ApifyInstagramClient(api, new InstagramNewsConfig.Properties("test-token", "test-task"));
    }

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    @DisplayName("✅ 마지막 실행 조회는 원격 실행 정보를 반환하며 404만 실행 없음으로 취급")
    void readsRemoteState() {
        responseBody = "{\"data\":{\"id\":\"run\",\"status\":\"RUNNING\"}}";
        assertThat(client.lastRun().path("id").asText()).isEqualTo("run");
        assertThat(requestUri.get()).isEqualTo("/actor-tasks/test-task/runs/last");
        assertThat(authorization.get()).isEqualTo("Bearer test-token");
        responseStatus = 404;
        assertThat(client.lastRun().isMissingNode()).isTrue();
        responseStatus = 401;
        assertThatThrownBy(client::lastRun).isInstanceOf(feign.FeignException.Unauthorized.class);
    }

    @Test
    @DisplayName("✅ 수집 요청은 기간과 결과 한도를 JSON으로 전달")
    void sendsCollectionInput() throws Exception {
        responseBody = "{\"data\":{\"id\":\"new\"}}";
        Instant since = Instant.parse("2026-02-01T00:00:00Z");
        assertThat(client.start(since)).isEqualTo("new");
        assertThat(requestUri.get()).isEqualTo("/actor-tasks/test-task/runs?timeout=3600");
        var input = mapper.readTree(requestBody.get());
        assertThat(input.path("onlyPostsNewerThan").asText()).isEqualTo(since.toString());
        assertThat(input.path("resultsLimit").asInt()).isEqualTo(1000);
        assertThat(input.path("username").get(0).asText()).isEqualTo("sopt_media_official");
    }

    @Test
    @DisplayName("✅ 데이터셋을 offset 없이 전체 조회하고 누락된 실행 ID는 거부")
    void readsWholeDataset() throws Exception {
        responseBody = mapper.writeValueAsString(List.of(post("1", "sopt_media_official", "2026-01-01T00:00:00Z")));
        assertThat(client.items("dataset")).hasSize(1);
        assertThat(requestUri.get()).contains("/datasets/dataset/items?", "limit=1001").doesNotContain("offset=");
        responseBody = "{\"data\":{}}";
        assertThatThrownBy(() -> client.start(Instant.now())).isInstanceOf(IllegalStateException.class)
                .hasMessage("Apify run ID missing");
    }
    @Test
    @DisplayName("✅ 같은 API 계정을 사용해도 개발·운영 Task의 조회와 실행 경로가 분리")
    void isolatesEnvironmentTasks() {
        var api = org.mockito.Mockito.mock(ApifyApi.class);
        var data = mapper.createObjectNode().set("data", mapper.createObjectNode().put("id", "run"));
        org.mockito.Mockito.when(api.lastRun(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(data);
        org.mockito.Mockito.when(api.start(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyMap())).thenReturn(data);
        var dev = new ApifyInstagramClient(api, new InstagramNewsConfig.Properties("same-token", "dev-task"));
        var prod = new ApifyInstagramClient(api, new InstagramNewsConfig.Properties("same-token", "prod-task"));
        dev.lastRun();
        prod.lastRun();
        dev.start(Instant.parse("2026-01-01T00:00:00Z"));
        prod.start(Instant.parse("2026-02-01T00:00:00Z"));
        org.mockito.Mockito.verify(api).lastRun("Bearer same-token", "dev-task");
        org.mockito.Mockito.verify(api).lastRun("Bearer same-token", "prod-task");
        org.mockito.Mockito.verify(api).start(org.mockito.ArgumentMatchers.eq("Bearer same-token"), org.mockito.ArgumentMatchers.eq("dev-task"), org.mockito.ArgumentMatchers.anyMap());
        org.mockito.Mockito.verify(api).start(org.mockito.ArgumentMatchers.eq("Bearer same-token"), org.mockito.ArgumentMatchers.eq("prod-task"), org.mockito.ArgumentMatchers.anyMap());
    }

    @Test
    @DisplayName("✅ 원격 INPUT은 JSON 본문을 그대로 읽고 만료되면 누락으로 반환")
    void readsPreviousInput() {
        responseBody = "{\"onlyPostsNewerThan\":\"2026-01-01T00:00:00Z\",\"resultsLimit\":1000}";
        assertThat(client.input("store").path("onlyPostsNewerThan").asText()).isEqualTo("2026-01-01T00:00:00Z");
        assertThat(requestUri.get()).isEqualTo("/key-value-stores/store/records/INPUT");
        responseStatus = 404;
        assertThat(client.input("store").isMissingNode()).isTrue();
    }

}
