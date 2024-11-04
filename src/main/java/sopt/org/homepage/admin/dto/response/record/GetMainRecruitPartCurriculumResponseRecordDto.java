package sopt.org.homepage.admin.dto.response.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "모집 파트 커리큘럼 정보")
@Builder
public record GetMainRecruitPartCurriculumResponseRecordDto(
        @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED) String part,
        @Schema(description = "소개글", requiredMode = Schema.RequiredMode.REQUIRED) GetMainIntroductionResponseRecordDto introduction
) {
}
