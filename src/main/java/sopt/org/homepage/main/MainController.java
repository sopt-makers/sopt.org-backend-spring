package sopt.org.homepage.main;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.main.dto.response.GetAboutPageResponseDto;
import sopt.org.homepage.main.dto.response.GetMainPageResponseDto;
import sopt.org.homepage.main.dto.response.GetRecruitingPageResponseDto;
import sopt.org.homepage.main.service.MainService;

@RestController
@RequiredArgsConstructor
@RequestMapping("homepage")
@Tag(name = "Homepage")
public class MainController {
    private final MainService mainService;

    @Operation(summary = "메인 페이지 조회", description = "메인 페이지 데이터를 조회합니다")
    @GetMapping("")
    public ResponseEntity<GetMainPageResponseDto> getMainPage () {
        GetMainPageResponseDto result = mainService.getMainPageData();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "소개 페이지 조회", description = "소개 페이지 데이터를 조회합니다")
    @GetMapping("about")
    public ResponseEntity<GetAboutPageResponseDto> getAboutPage () {
        GetAboutPageResponseDto result = mainService.getAboutPageData();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "지원하기 페이지 조회", description = "지원하기 페이지 데이터를 조회합니다")
    @GetMapping("recruit")
    public ResponseEntity<GetRecruitingPageResponseDto> get () {
        GetRecruitingPageResponseDto result = mainService.getRecruitingPageData();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}


