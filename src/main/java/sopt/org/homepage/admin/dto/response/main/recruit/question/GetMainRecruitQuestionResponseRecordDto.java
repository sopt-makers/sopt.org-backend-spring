package sopt.org.homepage.admin.dto.response.main.recruit.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "모집 질문 정보")
@Builder
public record GetMainRecruitQuestionResponseRecordDto(
        @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED) String part,
        @Schema(description = "질문 리스트", requiredMode = Schema.RequiredMode.REQUIRED) List<GetMainQuestionResponseRecordDto> questions
) {
}
