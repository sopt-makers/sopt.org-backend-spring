package sopt.org.homepage.admin.dto.response.main.recruit.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "모집 일정 정보")
@Builder
public record GetAdminRecruitScheduleResponseRecordDto(
        @Schema(description = "타입", example = "OB", allowableValues = {"OB", "YB"}, requiredMode = Schema.RequiredMode.REQUIRED) String type,
        @Schema(description = "일정", requiredMode = Schema.RequiredMode.REQUIRED) GetAdminScheduleResponseRecordDto schedule
) {
}
