package sopt.org.homepage.application.admin.dto.request.main.recruit.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "자주 묻는 질문")
@Getter
@NoArgsConstructor
public class AddAdminFAQRequestDto {
    @Schema(description = "FAQ 카테고리", example = "지원자격",
            allowableValues = {"지원자격", "면접", "활동"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "FAQ 카테고리는 필수입니다")
    @Pattern(regexp = "^\\s*(지원자격|면접|활동)\\s*$", message = "FAQ 카테고리는 지원자격, 면접, 활동 중 하나여야 합니다")
    private String category;

    @Schema(description = "질문 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "질문은 필수입니다")
    private List<@NotNull @Valid AddAdminQuestionRequestDto> questions;

}
