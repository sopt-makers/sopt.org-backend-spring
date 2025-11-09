package sopt.org.homepage.review.dto.request;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import sopt.org.homepage.common.dto.PaginateRequest;
import sopt.org.homepage.common.type.PartType;

@Validated
@Getter
@Schema(description = "리뷰 조회 요청")
public class ReviewsRequestDto extends PaginateRequest {
	@Parameter(description = "메인 카테고리 => [서류/면접] or [전체 활동](=활동 후기) 둘 중 하나의 값으로 넣어주시면 됩니다.", required = true)
	@Schema(allowableValues = {"서류/면접", "전체 활동"}, type = "string")
	private final String category;

	@Parameter(description = "활동 후기 세부 항목 => 카테고리에서 [전체 활동]을 선택한 경우에만 입력하면 됩니다. ([전체] 입력 시, 모두 조회)", required = false)
	@Schema(allowableValues = {"전체", "앱잼", "솝커톤", "세미나", "스터디", "솝텀", "메이커스"}, type = "string")
	private final String activity;

	@Parameter(description = "파트 => 전체를 불러올땐 아무값도 안넣으면 됩니다.", required = false)
	private final PartType partType;

	@Parameter(description = "활동기수 => 전체를 불러올땐 아무값도 안넣으면 됩니다.", required = false)
	private final Integer generation;

	public ReviewsRequestDto(Integer pageNo, Integer limit, String category, String activity,
                             PartType partType, Integer generation) {
		super(pageNo, limit);
		this.category = category;
		this.activity = activity;
		this.partType = partType;
		this.generation = generation;
	}
}
