package sopt.org.homepage.admin.dto.request.main.recruit.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.main.entity.sub.ScheduleEntity;

@Schema(description = "상세 일정")
@Getter
@NoArgsConstructor
public class AddAdminScheduleRequestDto {
    @Schema(description = "지원 시작 시간", example = "2024-01-01 09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicationStartTime;

    @Schema(description = "지원 종료 시간", example = "2024-01-31 18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicationEndTime;

    @Schema(description = "지원 결과 발표 시간", example = "2024-02-01 12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicationResultTime;

    @Schema(description = "면접 시작 시간", example = "2024-02-05 09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewStartTime;

    @Schema(description = "면접 종료 시간", example = "2024-02-05 18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewEndTime;

    @Schema(description = "최종 결과 발표 시간", example = "2024-02-10 12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String finalResultTime;

    public ScheduleEntity toEntity() {
        return ScheduleEntity.builder()
                .applicationStartTime(this.applicationStartTime)
                .applicationEndTime(this.applicationEndTime)
                .applicationResultTime(this.applicationResultTime)
                .interviewStartTime(this.interviewStartTime)
                .interviewEndTime(this.interviewEndTime)
                .finalResultTime(this.finalResultTime)
                .build();
    }
}
