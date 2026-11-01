package sopt.org.homepage.project;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        Comparator<ProjectsResponseDto> comparator = switch (dto.getSortBy()) {
            case GENERATION -> Comparator.comparing(ProjectsResponseDto::getGeneration,
                    Comparator.nullsLast(dto.getSortDirection() == GetProjectsRequestDto.SortDirection.ASC
                            ? Comparator.<Integer>naturalOrder() : Comparator.<Integer>reverseOrder()));
            case NAME -> Comparator.comparing(ProjectsResponseDto::getName,
                    Comparator.nullsLast(dto.getSortDirection() == GetProjectsRequestDto.SortDirection.ASC
                            ? Comparator.<String>naturalOrder() : Comparator.<String>reverseOrder()));
        };
        List<ProjectsResponseDto> allProjects = playgroundService.getAllProjects(dto).stream()
                .filter(p -> dto.getGeneration() == null || Objects.equals(dto.getGeneration(), p.getGeneration()))
                .filter(p -> dto.getIsAvailable() == null || Objects.equals(dto.getIsAvailable(), p.getIsAvailable()))
                .filter(p -> dto.getIsFounding() == null || Objects.equals(dto.getIsFounding(), p.getIsFounding()))
                .sorted(comparator)
                .toList();

        var paginatedProject = arrayUtil.paginateArray(allProjects, dto.getPageNo(), dto.getLimit());

        return new PaginateResponseDto<>(
                paginatedProject,
                allProjects.size(),
                dto.getLimit(),
                dto.getPageNo()
        );
    }

    public ProjectDetailResponseDto findOne(Long projectId) {
        return playgroundService.getProjectDetail(projectId);
    }
}
