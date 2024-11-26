package sopt.org.homepage.project;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.util.ArrayUtil;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.project.dto.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.ProjectsResponseDto;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final PlaygroundService playgroundService;
    private final ArrayUtil arrayUtil;


    public PaginateResponseDto<ProjectsResponseDto> paginateProjects(GetProjectsRequestDto dto) {
        val allProjects = findAll(dto);
        val paginatedProject = arrayUtil.paginateArray(allProjects, dto.getPageNo(), dto.getLimit());

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



}
