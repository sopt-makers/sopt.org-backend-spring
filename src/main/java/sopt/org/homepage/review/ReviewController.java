package sopt.org.homepage.review;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.review.dto.request.AddReviewRequestDto;
import sopt.org.homepage.review.dto.request.ReviewsInternalRequestDto;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsInternalResponseDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;
import sopt.org.homepage.review.service.ReviewService;

@Tag(name = "Reviews")
@RestController
@RequestMapping("reviews/legacy")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final AuthConfig authConfig;

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

	@PostMapping
	@Operation(summary = "활동후기 추가", description = "활동후기를 추가합니다.")
	public ResponseEntity<String> addReview(
		@Valid @RequestBody AddReviewRequestDto request,
		@RequestHeader("api-key") String apiKey
	) {
		if (apiKey == null) {
			throw new BusinessLogicException("api-key is required");
		}

		if (!apiKey.equals(authConfig.getApiKey())) {
			throw new BusinessLogicException("api-key is invalid");
		}

		reviewService.addReview(request);

		return ResponseEntity.ok("Success");
	}

	@Operation(summary = "Playground Internal 유저 활동후기 데이터 요청", description = "활동후기를 추가한 유저명을 입력하여 해당 유저가 추가한 활동후기 정보를 조회합니다.")
	@GetMapping("/internal")
	public ResponseEntity<ReviewsInternalResponseDto> getUserReviews(
		@ParameterObject @ModelAttribute ReviewsInternalRequestDto reviewsInternalRequestDto,
		@RequestHeader("api-key") String apiKey
	) {
		if (apiKey == null) {
			throw new BusinessLogicException("api-key is required");
		}

		if (!apiKey.equals(authConfig.getApiKey())) {
			throw new BusinessLogicException("api-key is invalid");
		}

		return ResponseEntity.ok(reviewService.getUserReviews(reviewsInternalRequestDto));
	}
}
