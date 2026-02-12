package sopt.org.homepage.infrastructure.external.playground;

import java.util.List;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

public interface PlaygroundService {
    List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest);

    ProjectDetailResponseDto getProjectDetail(Long projectId);

}
