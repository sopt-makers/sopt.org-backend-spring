package sopt.org.homepage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.dto.response.SemestersListResponse;
import sopt.org.homepage.service.SemestersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("semesters")
@Tag(name = "Semester")
public class SemestersController {
    private final SemestersService semestersService;

    @GetMapping("")
    public ResponseEntity<SemestersListResponse> getSemesters (
            @RequestParam(required = false, name = "limit") Integer limit,
            @RequestParam(required = false, name = "page") Integer page
    ) {
        val semesters = semestersService.findAll(limit, page);
        val count = semestersService.countAll();
        val response = new SemestersListResponse(
            page, limit, count, semesters
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


