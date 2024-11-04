package sopt.org.homepage.admin.dto.response.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "질문과 답변 정보")
@Builder
public record GetMainQuestionResponseRecordDto(
        @Schema(description = "질문", example = "몇명 뽑나요?", requiredMode = Schema.RequiredMode.REQUIRED) String question,
        @Schema(description = "답변", example = "10명 뽑아요.", requiredMode = Schema.RequiredMode.REQUIRED) String answer
) {
}
