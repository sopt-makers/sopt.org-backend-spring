package sopt.org.homepage.internal.playground;

import java.util.List;

import sopt.org.homepage.internal.playground.dto.PlaygroundMemberListResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectResponseDto;
import sopt.org.homepage.internal.playground.dto.PlaygroundUserResponse;
import sopt.org.homepage.internal.playground.dto.request.ScrapLinkRequestDto;
import sopt.org.homepage.internal.playground.dto.response.ScrapLinkResponseDto;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

public interface PlaygroundService {
	PlaygroundUserResponse getPlaygroundUserInfo(String authToken);

	List<ProjectsResponseDto> getAllProjects(GetProjectsRequestDto projectRequest);

	List<PlaygroundProjectResponseDto> getProjectsWithPagination();

	ProjectDetailResponseDto getProjectDetail(Long projectId);

	PlaygroundMemberListResponse getAllMembers(Integer generation);

	ScrapLinkResponseDto scrapLink(ScrapLinkRequestDto scrapLinkRequestDto);
}
