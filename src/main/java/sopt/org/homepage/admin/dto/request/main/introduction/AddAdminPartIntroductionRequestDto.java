package sopt.org.homepage.admin.dto.request.main.introduction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Schema(description = "파트 소개")
@Getter
@NoArgsConstructor
public class AddAdminPartIntroductionRequestDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명을 입력해주세요")
    private String part;

    @Schema(description = "파트 설명", example = "Android 앱 개발", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트 설명을 입력해주세요")
    private String description;

}
