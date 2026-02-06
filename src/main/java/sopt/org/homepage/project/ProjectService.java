package sopt.org.homepage.project;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.org.homepage.global.common.dto.PaginateResponseDto;
import sopt.org.homepage.global.common.util.ArrayUtil;
import sopt.org.homepage.infrastructure.external.playground.PlaygroundService;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final PlaygroundService playgroundService;
    private final ArrayUtil arrayUtil;

    public PaginateResponseDto<ProjectsResponseDto> paginateProjects(GetProjectsRequestDto dto) {
        var allProjects = findAll(dto);
        allProjects.sort(Comparator.comparing(
                ProjectsResponseDto::getGeneration,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        var paginatedProject = arrayUtil.paginateArray(allProjects, dto.getPageNo(), dto.getLimit());

        return new PaginateResponseDto<>(
                paginatedProject,
                allProjects.size(),
                dto.getLimit(),
                dto.getPageNo()
        );
    }

    public List<ProjectsResponseDto> findAll(GetProjectsRequestDto dto) {
        return playgroundService.getAllProjects(dto);
    }

    public ProjectDetailResponseDto findOne(Long projectId) {
        return playgroundService.getProjectDetail(projectId);
    }

    public List<ProjectsResponseDto> findByGeneration(Integer generation) {
        var allProjects = findAll(new GetProjectsRequestDto(1, Integer.MAX_VALUE, null, null));
        return allProjects.stream()
                .filter(project -> project.getGeneration() != null && project.getGeneration().equals(generation))
                .collect(Collectors.toList());
    }
}
