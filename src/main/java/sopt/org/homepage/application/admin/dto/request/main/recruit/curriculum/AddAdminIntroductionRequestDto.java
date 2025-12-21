package sopt.org.homepage.application.admin.dto.request.main.recruit.curriculum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "소개글 정보")
@Getter
@NoArgsConstructor
public class AddAdminIntroductionRequestDto {
    @Schema(description = "내용", example = "Android 앱 개발", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Schema(description = "우대사항", example = "Kotlin 개발 경험", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "우대사항은 필수입니다")
    private String preference;

}
