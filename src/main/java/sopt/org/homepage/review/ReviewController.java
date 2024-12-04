package sopt.org.homepage.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;

@Tag(name = "Reviews", description = "리뷰 API")
@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 목록 조회", description = "페이지네이션과 필터링을 지원하는 리뷰 목록 조회 API")
    @GetMapping
    public ResponseEntity<PaginateResponseDto<ReviewsResponseDto>> getReviews(
            @ModelAttribute ReviewsRequestDto reviewsRequestDto
    ) {
        return ResponseEntity.ok(reviewService.getReviews(reviewsRequestDto));
    }
}
