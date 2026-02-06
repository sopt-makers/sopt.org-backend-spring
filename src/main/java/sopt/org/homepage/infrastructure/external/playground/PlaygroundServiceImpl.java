package sopt.org.homepage.infrastructure.external.playground;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.global.common.constants.CacheType;
import sopt.org.homepage.global.common.mapper.ResponseMapper;
import sopt.org.homepage.global.common.util.ArrayUtil;
import sopt.org.homepage.global.config.AuthConfig;
import sopt.org.homepage.infrastructure.cache.CacheService;
import sopt.org.homepage.infrastructure.external.playground.dto.PlaygroundProjectAxiosResponseDto;
import sopt.org.homepage.infrastructure.external.playground.dto.PlaygroundProjectDetailResponse;
import sopt.org.homepage.infrastructure.external.playground.dto.PlaygroundProjectResponseDto;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;
import sopt.org.homepage.project.dto.type.ProjectType;
import sopt.org.homepage.project.dto.type.ServiceType;

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
    public List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest) {
        List<PlaygroundProjectResponseDto> rawProjects = fetchProjectsWithCache();
        List<ProjectsResponseDto> projects = convertToResponseDto(rawProjects);
        return applyFilters(projects, projectRequest.getFilter(), projectRequest.getPlatform());
    }

    @Override
    public ProjectDetailResponseDto getProjectDetail(Long projectId) {
        PlaygroundProjectDetailResponse projectResponse = playgroundClient.getProjectDetail(
                authConfig.getPlaygroundToken(), projectId
        );
        if (projectResponse == null) {
            throw new RuntimeException("프로젝트 데이터를 가져오지 못했습니다.");
        }
        return responseMapper.toProjectDetailResponse(projectResponse);
    }

    // ===== Private Methods =====

    /**
     * 캐시에서 프로젝트 목록 조회, 없으면 API 호출 후 캐시 저장
     */
    private List<PlaygroundProjectResponseDto> fetchProjectsWithCache() {
        List<PlaygroundProjectResponseDto> cached = cacheService.get(
                CacheType.PROJECT_LIST, PROJECT_CACHE_KEY, new TypeReference<>() {
                }
        );
        if (cached != null) {
            return cached;
        }

        try {
            List<PlaygroundProjectResponseDto> fetched = fetchAllFromApi();
            cacheService.put(CacheType.PROJECT_LIST, PROJECT_CACHE_KEY, fetched);
            return fetched;
        } catch (Exception e) {
            log.error("Failed to fetch projects from Playground API", e);
            return Collections.emptyList();
        }
    }

    /**
     * Playground API에서 커서 기반 페이지네이션으로 전체 프로젝트 조회
     */
    private List<PlaygroundProjectResponseDto> fetchAllFromApi() {
        final int limit = 20;
        int cursor = 0;
        int totalCount = 10;
        List<PlaygroundProjectResponseDto> response = new ArrayList<>();

        for (int i = 0; i < totalCount + 1; i = i + limit) {
            PlaygroundProjectAxiosResponseDto projectData = playgroundClient.getAllProjects(
                    limit, cursor
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

    /**
     * Playground 원시 데이터 → 응답 DTO 변환 (중복 제거 포함)
     */
    private List<ProjectsResponseDto> convertToResponseDto(
            List<PlaygroundProjectResponseDto> rawProjects) {
        List<PlaygroundProjectResponseDto> uniqueProjects = arrayUtil.dropDuplication(
                rawProjects, PlaygroundProjectResponseDto::name
        );

        return uniqueProjects.stream()
                .map(project -> responseMapper.toProjectResponse(
                        project.ProjectWithLink(
                                project.links() != null
                                        ? arrayUtil.dropDuplication(project.links(),
                                        PlaygroundProjectResponseDto.ProjectLinkResponse::linkId)
                                        : Collections.emptyList()
                        )
                ))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리, 플랫폼 필터 적용
     */
    private List<ProjectsResponseDto> applyFilters(
            List<ProjectsResponseDto> projects,
            ProjectType filter,
            ServiceType platform) {
        if (filter != null) {
            projects = projects.stream()
                    .filter(p -> p.getCategory().project().equals(filter))
                    .collect(Collectors.toList());
        }
        if (platform != null) {
            projects = projects.stream()
                    .filter(p -> p.getServiceType().contains(platform))
                    .collect(Collectors.toList());
        }
        return projects;
    }
}
