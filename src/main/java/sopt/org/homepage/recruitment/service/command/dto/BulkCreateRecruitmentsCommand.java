package sopt.org.homepage.recruitment.service.command.dto;

import lombok.Builder;

import java.util.List;

/**
 * BulkCreateRecruitmentsCommand
 *
 * 모집 일정 일괄 생성 커맨드 (Admin용)
 */
@Builder
public record BulkCreateRecruitmentsCommand(
        Integer generationId,
        List<RecruitmentData> recruitments
) {
    @Builder
    public record RecruitmentData(
            String type,  // 레거시 문자열 "OB" or "YB"
            ScheduleData schedule
    ) {
    }

    @Builder
    public record ScheduleData(
            String applicationStartTime,
            String applicationEndTime,
            String applicationResultTime,
            String interviewStartTime,
            String interviewEndTime,
            String finalResultTime
    ) {
    }
}