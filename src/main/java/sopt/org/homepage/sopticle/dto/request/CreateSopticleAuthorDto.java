package sopt.org.homepage.sopticle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "솝티클 작성자 정보")
public class CreateSopticleAuthorDto {

	@Schema(description = "작성자 id", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "작성자 ID는 필수입니다")
	private Long id;

	@Schema(description = "작성자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotEmpty(message = "작성자 이름은 필수입니다")
	private String name;

	@Schema(description = "작성자 프로필 이미지")
	private String profileImage;

	@Schema(description = "작성자 활동 기수", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "활동 기수는 필수입니다")
	private Integer generation;

	@Schema(description = "작성자 역할", requiredMode = Schema.RequiredMode.REQUIRED,
		example = "WEB")
	@NotNull(message = "역할은 필수입니다")
	private CreateSopticleAuthorRole part;
}
