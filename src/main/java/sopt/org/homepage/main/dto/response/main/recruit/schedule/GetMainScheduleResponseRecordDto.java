package sopt.org.homepage.main.dto.response.main.recruit.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "상세 일정 정보")
@Builder
public record GetMainScheduleResponseRecordDto(
        @Schema(description = "지원 시작 시간", example = "2024-01-01 09:00:00", requiredMode = Schema.RequiredMode.REQUIRED) String applicationStartTime,
        @Schema(description = "지원 종료 시간", example = "2024-01-31 18:00:00", requiredMode = Schema.RequiredMode.REQUIRED) String applicationEndTime,
        @Schema(description = "지원 결과 발표 시간", example = "2024-02-01 12:00:00", requiredMode = Schema.RequiredMode.REQUIRED) String applicationResultTime,
        @Schema(description = "면접 시작 시간", example = "2024-02-05 09:00:00", requiredMode = Schema.RequiredMode.REQUIRED) String interviewStartTime,
        @Schema(description = "면접 종료 시간", example = "2024-02-05 18:00:00", requiredMode = Schema.RequiredMode.REQUIRED) String interviewEndTime,
        @Schema(description = "최종 결과 발표 시간", example = "2024-02-10 12:00:00", requiredMode = Schema.RequiredMode.REQUIRED) String finalResultTime
) {
}
