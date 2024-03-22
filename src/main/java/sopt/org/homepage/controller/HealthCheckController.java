package sopt.org.homepage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "Defualt")
public class HealthCheckController {
    @GetMapping("")
    public ResponseEntity<String> healthCheck () {
        return ResponseEntity.status(HttpStatus.OK).body("HealthCheck Complete");
    }
}


