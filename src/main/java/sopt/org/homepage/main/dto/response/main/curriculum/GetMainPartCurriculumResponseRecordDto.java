package sopt.org.homepage.main.dto.response.main.curriculum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "파트별 커리큘럼 정보")
@Builder
public record GetMainPartCurriculumResponseRecordDto(
        @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED) String part,
        @Schema(description = "주차별 커리큘럼", requiredMode = Schema.RequiredMode.REQUIRED) List<GetMainCurriculumWeekResponseRecordDto> weeks
) {
}
