package sopt.org.homepage.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;
import sopt.org.homepage.review.service.ReviewService;

@Tag(name = "Reviews")
@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "활동 후기 가져오기")
    @GetMapping
    public ResponseEntity<PaginateResponseDto<ReviewsResponseDto>> getReviews(
            @ParameterObject @ModelAttribute ReviewsRequestDto reviewsRequestDto
    ) {
        return ResponseEntity.ok(reviewService.getReviews(reviewsRequestDto));
    }

    @GetMapping("/random")
    @Operation(summary = "랜덤 활동 후기 파트별로 하나씩 가져오기",
            description = "만약 특정 파트에 리뷰가 없다면 그 파트의 데이터는 나오지 않습니다.")
    public ResponseEntity<List<ReviewsResponseDto>> getRandomReviewByPart() {
        return ResponseEntity.ok(reviewService.getRandomReviewByPart());
    }
}
