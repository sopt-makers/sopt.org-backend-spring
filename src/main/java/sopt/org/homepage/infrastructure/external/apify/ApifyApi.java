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
    @PostMapping(value = "/actor-tasks/{taskId}/runs?timeout=3600", consumes = "application/json")
    JsonNode start(@RequestHeader("Authorization") String authorization, @PathVariable("taskId") String taskId,
                   @RequestBody Map<String, Object> input);

    @GetMapping("/actor-tasks/{taskId}/runs/last")
    JsonNode lastRun(@RequestHeader("Authorization") String authorization, @PathVariable("taskId") String taskId);

    @GetMapping("/key-value-stores/{storeId}/records/INPUT")
    JsonNode input(@RequestHeader("Authorization") String authorization, @PathVariable("storeId") String storeId);

    @GetMapping("/datasets/{id}/items?clean=true&format=json")
    List<InstagramPost> items(@RequestHeader("Authorization") String authorization,
                              @PathVariable("id") String dataset, @RequestParam("limit") int limit);
}
