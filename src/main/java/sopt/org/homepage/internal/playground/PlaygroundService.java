package sopt.org.homepage.internal.playground;

import sopt.org.homepage.internal.playground.dto.PlaygroundMemberListResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectResponseDto;
import sopt.org.homepage.internal.playground.dto.PlaygroundUserResponse;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

import java.util.List;

public interface PlaygroundService {
    PlaygroundUserResponse getPlaygroundUserInfo(String authToken);
    List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest);
    List<PlaygroundProjectResponseDto> getProjectsWithPagination();
    ProjectDetailResponseDto getProjectDetail(Long projectId);
    PlaygroundMemberListResponse getAllMembers(Integer generation);
}