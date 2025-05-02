package sopt.org.homepage.sopticle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "솝티클 생성 응답")
public class CreateSopticleResponseDto {
	@Schema(description = "솝티클 썸네일 이미지", requiredMode = Schema.RequiredMode.REQUIRED)
	private String thumbnailUrl;

	@Schema(description = "솝티클 제목", requiredMode = Schema.RequiredMode.REQUIRED)
	private String title;

	@Schema(description = "솝티클 설명", requiredMode = Schema.RequiredMode.REQUIRED)
	private String description;

	@Schema(description = "솝티클 URL", requiredMode = Schema.RequiredMode.REQUIRED)
	private String sopticleUrl;
}
