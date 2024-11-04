package sopt.org.homepage.admin.dto.response.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "주차별 커리큘럼 정보")
@Builder
public record GetMainCurriculumWeekResponseRecordDto(
        @Schema(description = "주차", example = "1", minimum = "1", maximum = "8", requiredMode = Schema.RequiredMode.REQUIRED) Integer week,
        @Schema(description = "커리큘럼 설명", example = "Android 기초 학습", requiredMode = Schema.RequiredMode.REQUIRED) String description
) {
}
