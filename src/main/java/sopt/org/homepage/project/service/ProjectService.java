package sopt.org.homepage.project.service;

import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

import java.util.List;

public interface ProjectService {
    PaginateResponseDto<ProjectsResponseDto> paginateProjects(GetProjectsRequestDto dto);
    List<ProjectsResponseDto> findAll(GetProjectsRequestDto dto);
    ProjectDetailResponseDto findOne(Long projectId);
    List<ProjectsResponseDto> findByGeneration(Integer generation);
}