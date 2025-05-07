package sopt.org.homepage.soptstory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "솝트스토리 생성 요청")
public class CreateSoptStoryDto {

	@Schema(description = "솝트스토리 주소", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotEmpty(message = "솝트스토리 주소는 필수입니다")
	private String link;
}
