package sopt.org.homepage.review.dto.request;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Validated
@Getter
@Schema(description = "Internal 유저 활동후기 요청 형식")
public class ReviewsInternalRequestDto {
	@Parameter(description = "활동후기 작성자명", required = true, example = "홍길동")
	private final String name;

	public ReviewsInternalRequestDto(String name) {
		this.name = name;
	}
}
