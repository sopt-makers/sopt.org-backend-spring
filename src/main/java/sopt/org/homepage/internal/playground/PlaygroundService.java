package sopt.org.homepage.internal.playground;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.common.util.ArrayUtil;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.internal.playground.dto.PlaygroundMemberListResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundUserResponse;
import sopt.org.homepage.project.dto.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.ProjectsResponseDto;
import sopt.org.homepage.common.mapper.ResponseMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class PlaygroundService {
    private final PlaygroundClient playgroundClient;
    private final ResponseMapper responseMapper;
    private final AuthConfig authConfig;
    private final ArrayUtil arrayUtil;

    public PlaygroundUserResponse getPlaygroundUserInfo(String authToken) {
        return playgroundClient.getPlaygroundUser(authToken);
    }

    public List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest) {
        val projectListResponse = playgroundClient.getAllProjects(authConfig.getPlaygroundToken());
        val uniqueResponse = arrayUtil.dropDuplication(projectListResponse, PlaygroundProjectResponse::name);
        val filter = projectRequest.getFilter();
        val platform = projectRequest.getPlatform();

        if (uniqueResponse == null) {
            return Collections.emptyList();
        }

        val uniqueLinkResponse = uniqueResponse.stream().map(response -> response.ProjectWithLink(
                arrayUtil.dropDuplication(response.links(), PlaygroundProjectResponse.ProjectLinkResponse::linkId))
        ).toList();

        List<ProjectsResponseDto> result = uniqueLinkResponse.stream().map(responseMapper::toProjectResponse).toList();

        if (filter != null) {
            result = result.stream()
                    .filter(element -> element.getCategory().project().equals(filter))
                    .collect(Collectors.toList());
        }
        if (platform != null) {
            result = result.stream()
                    .filter(element -> element.getServiceType().contains(platform))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public ProjectDetailResponseDto getProjectDetail(Long projectId){
        val projectResponse = playgroundClient.getProjectDetail(authConfig.getPlaygroundToken(), projectId);
        if (projectResponse == null) {
            throw new RuntimeException("프로젝트 데이터를 가져오지 못했습니다.");
        }

        return responseMapper.toProjectDetailResponse(projectResponse);
    }

    public PlaygroundMemberListResponse getAllMembers(String part, Integer generation) {
        return playgroundClient.getAllMembers(authConfig.getPlaygroundToken(), null, generation);
    }
}
