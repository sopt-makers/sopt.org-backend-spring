package sopt.org.homepage.internal.crew;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import sopt.org.homepage.internal.crew.dto.CrewMeetingResponse;

@FeignClient(value = "CrewClient", url = "${internal.crew.url}")
public interface CrewClient {
    //@GetMapping("/meeting/v2")
    @GetMapping("/meeting")
    CrewMeetingResponse getAllStudy(
            //ㅈ@RequestHeader("Authorization") String authToken,
            @RequestParam("page") Integer page,
            @RequestParam("take") Integer take,
            @RequestParam("createdGenerations") Integer generation,
            @RequestParam("isOnlyActiveGeneration") Boolean active,
            @RequestParam("category") String category
    );
}
