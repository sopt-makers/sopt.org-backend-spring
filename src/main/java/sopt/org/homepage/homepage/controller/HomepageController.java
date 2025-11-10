package sopt.org.homepage.homepage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.homepage.controller.dto.AboutPageResponse;
import sopt.org.homepage.homepage.controller.dto.MainPageResponse;
import sopt.org.homepage.homepage.controller.dto.RecruitPageResponse;
import sopt.org.homepage.homepage.service.HomepageQueryService;

/**
 * HomepageController
 *
 * 책임:
 * - User용 Homepage API 제공
 * - 기존 MainController를 대체
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("homepage/")
@Tag(name = "Homepage", description = "홈페이지 API")
public class HomepageController {

    private final HomepageQueryService homepageQueryService;

    @Operation(
            summary = "메인 페이지 조회",
            description = "메인 페이지 데이터를 조회합니다"
    )
    @GetMapping("")
    public ResponseEntity<MainPageResponse> getMainPage() {
        MainPageResponse response = homepageQueryService.getMainPageData();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "About 페이지 조회",
            description = "About 페이지 데이터를 조회합니다"
    )
    @GetMapping("about")
    public ResponseEntity<AboutPageResponse> getAboutPage() {
        AboutPageResponse response = homepageQueryService.getAboutPageData();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Recruiting 페이지 조회",
            description = "Recruiting 페이지 데이터를 조회합니다"
    )
    @GetMapping("recruit")
    public ResponseEntity<RecruitPageResponse> getRecruitPage() {
        RecruitPageResponse response = homepageQueryService.getRecruitPageData();
        return ResponseEntity.ok(response);
    }
}
