package sopt.org.homepage.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.global.common.dto.PaginateResponseDto;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;
import sopt.org.homepage.project.service.ProjectService;
import sopt.org.homepage.project.util.ProjectComparator;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects")
@Tag(name = "Project")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("")
    @Operation(summary = "프로젝트 정보 전부 가져오기")
    public ResponseEntity<PaginateResponseDto<ProjectsResponseDto>> getProjects(
            @ParameterObject @ModelAttribute GetProjectsRequestDto getProjectsRequestDto
    ) {
        PaginateResponseDto<ProjectsResponseDto> projects = projectService.paginateProjects(getProjectsRequestDto);

        var sortedData = new ArrayList<>(projects.getData());
        sortedData.sort(ProjectComparator::compare);

        var sortedProjects = new PaginateResponseDto<>(
                sortedData,
                projects.getTotalCount(),
                projects.getLimit(),
                projects.getCurrentPage()
        );
        return ResponseEntity.status(HttpStatus.OK).body(sortedProjects);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "특정 프로젝트 정보 가져오기")
    public ResponseEntity<ProjectDetailResponseDto> getProject(
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(projectService.findOne(projectId));
    }

}


