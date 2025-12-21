package sopt.org.homepage.application.admin.dto.response.main.recruit.question;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "모집 질문 정보")
@Builder
public record GetAdminRecruitQuestionResponseRecordDto(
        @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED) String part,
        @Schema(description = "질문 리스트", requiredMode = Schema.RequiredMode.REQUIRED) List<GetAdminQuestionResponseRecordDto> questions
) {
}
