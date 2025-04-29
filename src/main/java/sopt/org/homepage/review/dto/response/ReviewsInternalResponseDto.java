package sopt.org.homepage.review.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "플레이그라운드 Internal 유저 활동후기 데이터 응답")
@Getter
public class ReviewsInternalResponseDto {

	@Schema(description = "유저가 작성한 활동후기 개수", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
	private final int reviewCount;

	private final List<ReviewsResponseDto> reviews;

	@Builder
	private ReviewsInternalResponseDto(Integer reviewCount, List<ReviewsResponseDto> reviews) {
		this.reviewCount = reviewCount;
		this.reviews = reviews;
	}
}
