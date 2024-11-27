package sopt.org.homepage.project;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.util.ProjectComparator;
import sopt.org.homepage.project.dto.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.ProjectsResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects")
@Tag(name = "Project")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("")
    public ResponseEntity<PaginateResponseDto<ProjectsResponseDto>> getProjects (
            @ParameterObject @ModelAttribute GetProjectsRequestDto getProjectsRequestDto
    ) {
        PaginateResponseDto<ProjectsResponseDto> projects = projectService.paginateProjects(getProjectsRequestDto);

        val sortedData = new ArrayList<>(projects.getData());
        sortedData.sort(ProjectComparator::compare);

        val sortedProjects = new PaginateResponseDto<>(
                sortedData,
                projects.getTotalCount(),
                projects.getLimit(),
                projects.getCurrentPage()
        );
        return ResponseEntity.status(HttpStatus.OK).body(sortedProjects);
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponseDto> getProject(
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(projectService.findOne(projectId));
    }

}


