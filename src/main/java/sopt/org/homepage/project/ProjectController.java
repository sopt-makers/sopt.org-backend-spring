package sopt.org.homepage.project;

import lombok.val;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.common.dto.PaginateResponse;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.common.mapper.ResponseMapper;
import sopt.org.homepage.project.dto.GetAllProjectRequest;
import sopt.org.homepage.project.dto.ProjectResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects")
@Tag(name = "Project")
public class ProjectController {
    private final ResponseMapper responseMapper;
    private final ProjectService projectService;
    private final PlaygroundService playgroundService;

    @GetMapping("")
    public ResponseEntity<PaginateResponse<ProjectResponse>> getAllProject (
            @ParameterObject @ModelAttribute GetAllProjectRequest getAllProjectDto
    ) {
        val testProject = playgroundService.getAllProjects(getAllProjectDto);
        val testDetail = playgroundService.getProjectDetail(testProject.get(0).getId());
        System.out.println(testDetail);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}


