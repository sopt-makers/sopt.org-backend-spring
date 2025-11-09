package sopt.org.homepage.recruitment.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.recruitment.domain.Recruitment;

/**
 * RecruitmentView
 *
 * 모집 일정 조회 DTO
 */
@Builder
public record RecruitmentView(
        Long id,
        Integer generationId,
        String type,  // 레거시 호환용 문자열
        ScheduleView schedule
) {
    public static RecruitmentView from(Recruitment recruitment) {
        return RecruitmentView.builder()
                .id(recruitment.getId())
                .generationId(recruitment.getGenerationId())
                .type(recruitment.getRecruitType().getCode())  // Enum → 문자열
                .schedule(ScheduleView.from(recruitment.getSchedule()))
                .build();
    }

    @Builder
    public record ScheduleView(
            String applicationStartTime,
            String applicationEndTime,
            String applicationResultTime,
            String interviewStartTime,
            String interviewEndTime,
            String finalResultTime
    ) {
        public static ScheduleView from(sopt.org.homepage.recruitment.domain.vo.Schedule schedule) {
            return ScheduleView.builder()
                    .applicationStartTime(schedule.getApplicationStartTime())
                    .applicationEndTime(schedule.getApplicationEndTime())
                    .applicationResultTime(schedule.getApplicationResultTime())
                    .interviewStartTime(schedule.getInterviewStartTime())
                    .interviewEndTime(schedule.getInterviewEndTime())
                    .finalResultTime(schedule.getFinalResultTime())
                    .build();
        }
    }
}