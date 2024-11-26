package sopt.org.homepage.project;

import lombok.val;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.common.mapper.ResponseMapper;
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
        val projects = projectService.paginateProjects(getProjectsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponseDto> getProject(
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(projectService.findOne(projectId));
    }

}


