package sopt.org.homepage.news.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramPost(String id, String ownerUsername, String caption, String url,
                            Instant timestamp, String displayUrl, List<InstagramPost> childPosts) {
    /** 첫 번째 자식 이미지가 없으면 부모의 커버 이미지를 사용한다. */
    public String thumbnail() {
        if (childPosts != null && !childPosts.isEmpty()) {
            InstagramPost first = childPosts.get(0);
            if (first != null && first.displayUrl() != null && !first.displayUrl().isBlank()) {
                return first.displayUrl();
            }
        }
        return displayUrl;
    }
}
