package sopt.org.homepage.application.recruitpage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.application.recruitpage.dto.RecruitMainPageResponse;
import sopt.org.homepage.application.recruitpage.service.RecruitPageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("recruit")
@Tag(name = "Recruit Page", description = "지원서 API")
public class RecruitPageController {

    private final RecruitPageService recruitPageService;

    @Operation(
            summary = "지원서 메인 페이지 조회",
            description = "지원서 메인 페이지 데이터를 조회합니다"
    )
    @GetMapping("")
    ResponseEntity<RecruitMainPageResponse> getRecruitMainPage(){
        return ResponseEntity.ok(recruitPageService.getRecruitMainPageData());
    }
}
