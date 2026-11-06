package sopt.org.homepage.news.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApifyInstagramClient {
    public static final String ACCOUNT = "sopt_media_official";
    private final RestClient client;

    public ApifyInstagramClient(InstagramNewsConfig.Properties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(60));
        client = RestClient.builder().baseUrl("https://api.apify.com/v2")
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + properties.apifyToken()).build();
    }

    /** 2026년 KST 이후 계정 게시물을 비동기로 수집하고 실행 ID를 반환한다. */
    public String start() {
        JsonNode response = client.post().uri("/acts/apify~instagram-post-scraper/runs?timeout=3600")
                .body(Map.of("username", List.of(ACCOUNT), "resultsLimit", 10000,
                        "onlyPostsNewerThan", "2025-12-31T15:00:00Z", "skipPinnedPosts", false))
                .retrieve().body(JsonNode.class);
        String id = response.path("data").path("id").asText();
        if (id.isBlank()) throw new IllegalStateException("Apify run ID missing");
        return id;
    }

    /** 수집 완료 여부를 판단할 실행 상태와 dataset ID를 조회한다. */
    public JsonNode run(String id) {
        return client.get().uri("/actor-runs/{id}", id).retrieve().body(JsonNode.class).path("data");
    }

    /** 완료된 dataset을 오프셋 기준으로 10건씩 읽는다. */
    public List<InstagramPost> items(String dataset, int offset) {
        List<InstagramPost> posts = client.get()
                .uri("/datasets/{id}/items?clean=true&format=json&offset={offset}&limit=10", dataset, offset)
                .retrieve().body(new ParameterizedTypeReference<List<InstagramPost>>() {});
        if (posts == null) throw new IllegalStateException("Apify dataset missing");
        return posts;
    }
}
