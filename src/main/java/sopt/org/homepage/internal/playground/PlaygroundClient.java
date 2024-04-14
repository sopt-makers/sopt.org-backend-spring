package sopt.org.homepage.internal.playground;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import sopt.org.homepage.internal.playground.dto.*;

import java.util.List;

@FeignClient(value = "PlaygroundClient", url = "${internal.playground.url}")
public interface PlaygroundClient {
    @GetMapping("/internal/api/v1/members/me")
    PlaygroundUserResponse getPlaygroundUser(
            @RequestHeader("Authorization") String authToken
    );

    @GetMapping("/internal/api/v1/projects")
    List<PlaygroundProjectResponse> getAllProjects(
            @RequestHeader("Authorization") String playgroundToken
    );

    @GetMapping("/internal/api/v1/projects/{projectId}")
    PlaygroundProjectDetailResponse getProjectDetail(
            @RequestHeader("Authorization") String playgroundToken,
            @PathVariable("projectId") Long projectId
    );

    @GetMapping("/internal/api/v1/official/members/profile")
    PlaygroundMemberListResponse getAllMembers(
            @RequestHeader("Authorization") String playgroundToken,
            @RequestParam("filter") String part,
            @RequestParam("generation") Integer generation
    );


}
