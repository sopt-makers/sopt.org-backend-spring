package sopt.org.homepage.health;

import lombok.val;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.internal.crew.CrewService;
import sopt.org.homepage.internal.crew.dto.StudyResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "Default")
public class HealthCheckController {
    private final CrewService crewService;
    @GetMapping("")
    public ResponseEntity<String> healthCheck () {
        return ResponseEntity.status(HttpStatus.OK).body("HealthCheck Complete");
    }

    @GetMapping("/test")
    public ResponseEntity<List<StudyResponse>> testAPI () {
        val response = crewService.getAllStudy(null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


