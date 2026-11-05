package sopt.org.homepage.news.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramPost(String id, String ownerUsername, String caption, String url,
                            Instant timestamp, String displayUrl, List<InstagramPost> childPosts) {
    public String thumbnail() {
        return childPosts != null && !childPosts.isEmpty() ? childPosts.get(0).displayUrl() : displayUrl;
    }
}
