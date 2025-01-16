package sopt.org.homepage.internal.playground;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.common.constants.CacheType;
import sopt.org.homepage.common.mapper.ResponseMapper;
import sopt.org.homepage.common.util.ArrayUtil;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.internal.playground.dto.*;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaygroundServiceImpl implements PlaygroundService {
    private final PlaygroundClient playgroundClient;
    private final ResponseMapper responseMapper;
    private final AuthConfig authConfig;
    private final ArrayUtil arrayUtil;
    private final CacheService cacheService;
    private static final String PROJECT_CACHE_KEY = "all_projects";

    @Override
    public PlaygroundUserResponse getPlaygroundUserInfo(String authToken) {
        return playgroundClient.getPlaygroundUser(authToken);
    }

    @Override
    public List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest) {
        List<PlaygroundProjectResponseDto> projectListResponse = cacheService.get(
                CacheType.PROJECT_LIST, PROJECT_CACHE_KEY, new TypeReference<>() {
                }
        );

        if (projectListResponse == null) {
            try {
                projectListResponse = getProjectsWithPagination();
                cacheService.put(CacheType.PROJECT_LIST, PROJECT_CACHE_KEY, projectListResponse);
            } catch (Exception e) {
                log.error("Project API Failed to fetch projects", e);
                return Collections.emptyList();
            }
        }

        List<ProjectsResponseDto> result = new ArrayList<>();
        var filter = projectRequest.getFilter();
        var platform = projectRequest.getPlatform();

        var uniqueResponse = arrayUtil.dropDuplication(projectListResponse, PlaygroundProjectResponseDto::name);
        var uniqueLinkResponse = uniqueResponse.stream()
                .map(response -> response.ProjectWithLink(
                        arrayUtil.dropDuplication(response.links(), PlaygroundProjectResponseDto.ProjectLinkResponse::linkId)
                ))
                .toList();

        if (uniqueLinkResponse.isEmpty()) {
            return Collections.emptyList();
        }

        for (var data : projectListResponse) {
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

    @Override
    public List<PlaygroundProjectResponseDto> getProjectsWithPagination() {
        final int limit = 20;
        int cursor = 0;
        int totalCount = 10;
        List<PlaygroundProjectResponseDto> response = new ArrayList<>();

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

    @Override
    public ProjectDetailResponseDto getProjectDetail(Long projectId) {
        var projectResponse = playgroundClient.getProjectDetail(authConfig.getPlaygroundToken(), projectId);
        if (projectResponse == null) {
            throw new RuntimeException("프로젝트 데이터를 가져오지 못했습니다.");
        }

        return responseMapper.toProjectDetailResponse(projectResponse);
    }

    @Override
    public PlaygroundMemberListResponse getAllMembers(Integer generation) {
        return playgroundClient.getAllMembers(authConfig.getPlaygroundToken(), null, generation);
    }
}