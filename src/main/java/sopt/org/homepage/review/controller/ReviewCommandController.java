package sopt.org.homepage.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.global.config.AuthConfig;
import sopt.org.homepage.global.exception.BusinessLogicException;
import sopt.org.homepage.review.controller.dto.CreateReviewReq;
import sopt.org.homepage.review.service.command.ReviewCommandService;
import sopt.org.homepage.review.service.command.dto.CreateReviewCommand;
import sopt.org.homepage.infrastructure.external.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.infrastructure.external.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.infrastructure.external.scrap.service.ScraperService;

/**
 * 리뷰 Command Controller (쓰기 전용)
 * <p>
 * 책임: - 리뷰 생성 API - API 키 검증 - 외부 스크래핑 서비스 호출
 */
@Tag(name = "Reviews - Command", description = "리뷰 쓰기 API")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewCommandController {

    private final ReviewCommandService reviewCommandService;
    private final ScraperService scraperService;
    private final AuthConfig authConfig;

    @PostMapping
    @Operation(
            summary = "활동후기 추가",
            description = "활동후기를 추가합니다. API 키 인증이 필요합니다."
    )
    public ResponseEntity<String> createReview(
            @Valid @RequestBody CreateReviewReq request,
            @RequestHeader("api-key") String apiKey
    ) {
        // 1. API 키 검증
        validateApiKey(apiKey);

        // 2. 외부 스크래핑 서비스 호출
        CreateScraperResponseDto scrapResult = scraperService.scrap(
                new ScrapArticleDto(request.link())
        );

        // 3. Command 생성 (변환 로직은 Command DTO가 담당)
        CreateReviewCommand command = CreateReviewCommand.from(request, scrapResult);

        // 4. 리뷰 생성
        reviewCommandService.createReview(command);

        return ResponseEntity.ok("Success");
    }

    /**
     * API 키 검증
     */
    private void validateApiKey(String apiKey) {
        if (apiKey == null) {
            throw new BusinessLogicException("api-key is required");
        }
        if (!apiKey.equals(authConfig.getApiKey())) {
            throw new BusinessLogicException("api-key is invalid");
        }
    }

}
