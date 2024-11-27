package sopt.org.homepage.internal.playground;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.common.constants.CacheType;
import sopt.org.homepage.common.util.ArrayUtil;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.internal.playground.dto.PlaygroundMemberListResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectAxiosResponseDto;
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

    private final CacheService cacheService;

    private static final String PROJECT_CACHE_KEY = "all_projects";



    public PlaygroundUserResponse getPlaygroundUserInfo(String authToken) {
        return playgroundClient.getPlaygroundUser(authToken);
    }

    public List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest) {

        // 캐시에서 데이터 조회 시도
        List<PlaygroundProjectResponse> projectListResponse = cacheService.get(
                CacheType.PROJECT_LIST,
                PROJECT_CACHE_KEY,
                new TypeReference<List<PlaygroundProjectResponse>>() {}

        );

        // 캐시 미스인 경우 API 호출
        if (projectListResponse == null) {
            try {
                projectListResponse = getProjectsWithPagination();
                // 캐시에 저장
                cacheService.put(CacheType.PROJECT_LIST, PROJECT_CACHE_KEY, projectListResponse);
            } catch (Exception e) {
                log.error("Failed to fetch projects", e);
                return Collections.emptyList();
            }
        }



        List<ProjectsResponseDto> result = new ArrayList<>();
        val filter = projectRequest.getFilter();
        val platform = projectRequest.getPlatform();

        val uniqueResponse = arrayUtil.dropDuplication(projectListResponse, PlaygroundProjectResponse::name);

        val uniqueLinkResponse = uniqueResponse.stream()
                .map(response -> response.ProjectWithLink(
                        arrayUtil.dropDuplication(response.links(), PlaygroundProjectResponse.ProjectLinkResponse::linkId)
                ))
                .toList();


        if (uniqueLinkResponse == null) {
            return Collections.emptyList();
        }

        for (val data : projectListResponse) {
            result.add(responseMapper.toProjectResponse(data));
        }

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


    public List<PlaygroundProjectResponse> getProjectsWithPagination() {
        final int limit = 20;
        int cursor = 0;
        int totalCount = 10;
        List<PlaygroundProjectResponse> response = new ArrayList<>();

        for (int i = 0; i < totalCount + 1; i = i + limit) {
            PlaygroundProjectAxiosResponseDto projectData = playgroundClient.getAllProjects(
                    authConfig.getPlaygroundToken(),
                    limit,
                    cursor
            );

            if (projectData.projectList().isEmpty()) {
                break;
            }

            totalCount = projectData.totalCount();
            response.addAll(projectData.projectList());

            int lastDataIdx = projectData.projectList().size() - 1;
            cursor = projectData.projectList().get(lastDataIdx).id().intValue();

            if (!projectData.hasNext()) {
                break;
            }
        }

        return response;
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
