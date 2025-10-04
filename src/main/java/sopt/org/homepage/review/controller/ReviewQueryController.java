package sopt.org.homepage.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.review.controller.dto.*;
import sopt.org.homepage.review.service.query.ReviewQueryService;
import sopt.org.homepage.review.service.query.dto.ReviewSearchCond;
import sopt.org.homepage.review.service.query.dto.ReviewSummaryView;
import sopt.org.homepage.review.service.query.dto.ReviewsByAuthorView;

import java.util.List;

/**
 * 리뷰 Query Controller (읽기 전용)
 */
@Tag(name = "Reviews - Query", description = "리뷰 조회 API")
@RestController
@RequestMapping("/reviews/v2")
@RequiredArgsConstructor
public class ReviewQueryController {

    private final ReviewQueryService reviewQueryService;
    private final AuthConfig authConfig;

    @GetMapping
    @Operation(summary = "활동 후기 목록 조회")
    public ResponseEntity<PaginateResponseDto<ReviewRes>> getReviews(
            @ParameterObject @ModelAttribute ReviewSearchReq request
    ) {
        // 1. 검색 조건 생성 (Record의 간결한 생성자)
        ReviewSearchCond cond = new ReviewSearchCond(
                request.category(),
                request.activity(),
                request.part(),
                request.generation()
        );

        // 2. 리뷰 조회
        List<ReviewSummaryView> views = reviewQueryService.searchReviews(
                cond,
                request.getOffset(),
                request.limit()
        );

        // 3. 총 개수 조회
        long totalCount = reviewQueryService.countReviews(cond);

        // 4. Response 변환
        List<ReviewRes> responses = views.stream()
                .map(ReviewRes::from)
                .toList();

        return ResponseEntity.ok(
                new PaginateResponseDto<>(
                        responses,
                        (int) totalCount,
                        request.limit(),
                        request.pageNo()
                )
        );
    }

    @GetMapping("/random")
    @Operation(summary = "랜덤 활동 후기 파트별로 하나씩 조회")
    public ResponseEntity<List<ReviewRes>> getRandomReviewsByPart() {
        List<ReviewSummaryView> views = reviewQueryService.getRandomReviewsByPart();

        List<ReviewRes> responses = views.stream()
                .map(ReviewRes::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/internal")
    @Operation(summary = "Playground Internal - 유저 활동후기 데이터 조회")
    public ResponseEntity<ReviewsByAuthorRes> getReviewsByAuthor(
            @ParameterObject @ModelAttribute @Valid ReviewsByAuthorReq request,
            @RequestHeader("api-key") String apiKey
    ) {
        // 1. API 키 검증
        validateApiKey(apiKey);

        // 2. 작성자별 리뷰 조회
        ReviewsByAuthorView view = reviewQueryService.getReviewsByAuthor(request.name());

        // 3. Response 변환
        ReviewsByAuthorRes response = ReviewsByAuthorRes.from(view);

        return ResponseEntity.ok(response);
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