package sopt.org.homepage.infrastructure.external.playground;

import java.util.List;
import sopt.org.homepage.infrastructure.external.playground.dto.PlaygroundProjectResponseDto;
import sopt.org.homepage.infrastructure.external.playground.dto.request.ScrapLinkRequestDto;
import sopt.org.homepage.infrastructure.external.playground.dto.response.ScrapLinkResponseDto;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

public interface PlaygroundService {
    List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest);

    List<PlaygroundProjectResponseDto> getProjectsWithPagination();

    ProjectDetailResponseDto getProjectDetail(Long projectId);

    ScrapLinkResponseDto scrapLink(ScrapLinkRequestDto scrapLinkRequestDto);
}
