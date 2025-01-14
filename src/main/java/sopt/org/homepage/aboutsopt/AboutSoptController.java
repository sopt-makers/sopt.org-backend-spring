package sopt.org.homepage.aboutsopt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.aboutsopt.dto.GetAboutSoptResponseDto;
import sopt.org.homepage.aboutsopt.service.AboutSoptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("aboutsopt")
@Tag(name = "AboutSopt")
public class AboutSoptController {
    private final AboutSoptService aboutSoptService;

    @GetMapping("")
    @Operation(summary = "사용자용 AboutSopt 조회를 조회합니다, Query값이 null이면 최근 기수를 불러옵니다. 해당 기수의 AboutSOPT가  없으면 not found error"
           )
    public ResponseEntity<GetAboutSoptResponseDto> getAboutSopt(
            @Parameter(description = "기수")
            @RequestParam(required = false) Integer generation
    ) {
        return ResponseEntity.ok(aboutSoptService.getAboutSopt(generation));
    }
}
