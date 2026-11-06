package sopt.org.homepage.infrastructure.external.apify;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.news.instagram.InstagramPost;

@FeignClient(name = "ApifyApi", url = "${news.instagram.api-url:https://api.apify.com/v2}",
        configuration = ApifyFeignConfig.class)
public interface ApifyApi {
    @PostMapping("/acts/apify~instagram-post-scraper/runs?timeout=3600")
    JsonNode start(@RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> input);

    @GetMapping("/acts/apify~instagram-post-scraper/runs/last")
    JsonNode lastRun(@RequestHeader("Authorization") String authorization);

    @GetMapping("/datasets/{id}/items?clean=true&format=json")
    List<InstagramPost> items(@RequestHeader("Authorization") String authorization,
                              @PathVariable("id") String dataset, @RequestParam("limit") int limit);
}
