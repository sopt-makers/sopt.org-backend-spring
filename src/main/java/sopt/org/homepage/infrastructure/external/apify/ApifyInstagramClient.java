package sopt.org.homepage.infrastructure.external.apify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import feign.FeignException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sopt.org.homepage.news.instagram.InstagramNewsConfig;
import sopt.org.homepage.news.instagram.InstagramPost;

@Component
@RequiredArgsConstructor
public class ApifyInstagramClient {
    public static final String ACCOUNT = "sopt_media_official";
    private final ApifyApi api;
    private final InstagramNewsConfig.Properties properties;

    public String start(Instant since) {
        JsonNode response = api.start(authorization(), properties.taskId(), Map.of("username", List.of(ACCOUNT),
                "resultsLimit", properties.resultsLimit(), "onlyPostsNewerThan", since.toString(),
                "skipPinnedPosts", false));
        String id = response.path("data").path("id").asText();
        if (id.isBlank()) throw new IllegalStateException("Apify run ID missing");
        return id;
    }

    /** 실행 상태의 원본은 Apify이며, 로컬 실행 ID를 보관하지 않는다. */
    public JsonNode lastRun() {
        try {
            return api.lastRun(authorization(), properties.taskId()).path("data");
        } catch (FeignException.NotFound e) {
            return MissingNode.getInstance();
        }
    }

    /** 한 번에 전체 결과를 읽는다. 한도 초과는 잘라 저장하지 않고 오류로 처리한다. */
    public List<InstagramPost> items(String dataset) {
        List<InstagramPost> posts = api.items(authorization(), dataset, properties.resultsLimit() + 1);
        if (posts == null) throw new IllegalStateException("Apify dataset missing");
        if (posts.size() > properties.resultsLimit()) throw new IllegalStateException("Apify dataset exceeds configured limit");
        return posts;
    }

    private String authorization() {
        return "Bearer " + properties.apifyToken();
    }
}
