package sopt.org.homepage.application.admin.dto.response.main.recruit.question;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "자주 묻는 질문 정보")
@Builder
public record GetAdminFAQResponseRecordDto(
        @Schema(description = "FAQ 카테고리", example = "지원자격", allowableValues = {"지원자격", "면접",
                "활동"}, requiredMode = Schema.RequiredMode.REQUIRED) String category,
        @Schema(description = "질문 리스트", requiredMode = Schema.RequiredMode.REQUIRED) List<GetAdminQuestionResponseRecordDto> questions
) {
}
