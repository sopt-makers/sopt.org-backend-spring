package sopt.org.homepage.application.admin.dto.response.main.activityschedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "활동 전체 일정 정보")
@Builder
public record GetAdminActivityScheduleResponseRecordDto(
        @Schema(description = "일정명", example = "OT") String name,
        @Schema(description = "시작 날짜 (yyyy-MM-dd)", example = "2026-03-28") String startDate,
        @Schema(description = "종료 날짜 (yyyy-MM-dd), 단일 날짜인 경우 null", example = "2026-03-29") String endDate
) {
}
