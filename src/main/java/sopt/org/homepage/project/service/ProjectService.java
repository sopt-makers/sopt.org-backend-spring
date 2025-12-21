package sopt.org.homepage.project.service;

import java.util.List;
import sopt.org.homepage.global.common.dto.PaginateResponseDto;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

public interface ProjectService {
    PaginateResponseDto<ProjectsResponseDto> paginateProjects(GetProjectsRequestDto dto);

    List<ProjectsResponseDto> findAll(GetProjectsRequestDto dto);

    ProjectDetailResponseDto findOne(Long projectId);

    List<ProjectsResponseDto> findByGeneration(Integer generation);
}
