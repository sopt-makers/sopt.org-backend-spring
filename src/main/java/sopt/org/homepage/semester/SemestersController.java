package sopt.org.homepage.semester;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.semester.dto.response.SemestersListResponse;
import sopt.org.homepage.mapper.ResponseMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("semesters")
@Tag(name = "Semester")
public class SemestersController {
    private final ResponseMapper responseMapper;
    private final SemestersService semestersService;

    @GetMapping("")
    public ResponseEntity<SemestersListResponse> getSemesters (
            @RequestParam(required = true, name = "limit") Integer limit,
            @RequestParam(required = true, defaultValue = "1", name = "page") Integer page
    ) {
        val semesters = semestersService.findAll(limit, page);
        val count = semestersService.countAll();
        val semestersResponse = semesters.stream().map(responseMapper::toSemestersResponse).toList();
        val response = new SemestersListResponse(
            page, limit, count, semestersResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


