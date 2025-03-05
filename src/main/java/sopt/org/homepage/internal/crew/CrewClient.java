package sopt.org.homepage.internal.crew;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sopt.org.homepage.internal.crew.dto.StudyCountResponse;

@FeignClient(value = "CrewClient", url = "${internal.crew.url}")
public interface CrewClient {
    @GetMapping("/internal/meeting/stats/studies")
    StudyCountResponse getStudyCount(
            @RequestParam("generation") Integer generation
    );
}
