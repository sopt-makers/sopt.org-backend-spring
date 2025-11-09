package sopt.org.homepage.review.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.common.type.PartType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "활동후기 추가 요청")
public class AddReviewRequestDto {

	@Schema(description = "활동 기수", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotEmpty(message = "기수 정보는 필수입니다")
	private Integer generation;

	@Schema(description = "파트", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotEmpty(message = "파트 정보는 필수입니다")
	private PartType partType;

	@Schema(description = "메인 유형", requiredMode = Schema.RequiredMode.REQUIRED, example = "전체 활동")
	@NotEmpty(message = "메인 유형는 필수입니다")
	private AddReviewMainCategory mainCategory;

	@Schema(description = "활동 유형 (메인 유형 => 전체활동 선택 시 입력)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private List<AddReviewSubActivity> subActivities;

	@Schema(description = "활동 유형 (메인 유형 => 서류/면접 선택 시 입력)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private AddReviewSubRecruiting subRecruiting;

	@Schema(description = "작성자명", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotEmpty(message = "작성자명 정보는 필수입니다")
	private String author;

	@Schema(description = "작성자 프로필 이미지", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String authorProfileImageUrl;

	@Schema(description = "활동후기 링크", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotEmpty(message = "활동후기 링크는 필수입니다")
	private String link;
}
