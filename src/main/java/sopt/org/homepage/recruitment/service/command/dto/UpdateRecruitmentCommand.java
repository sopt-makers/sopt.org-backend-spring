package sopt.org.homepage.recruitment.service.command.dto;

import lombok.Builder;

/**
 * UpdateRecruitmentCommand
 *
 * 모집 일정 수정 커맨드
 */
@Builder
public record UpdateRecruitmentCommand(
        Long id,
        ScheduleCommand schedule
) {
    @Builder
    public record ScheduleCommand(
            String applicationStartTime,
            String applicationEndTime,
            String applicationResultTime,
            String interviewStartTime,
            String interviewEndTime,
            String finalResultTime
    ) {
        public sopt.org.homepage.recruitment.domain.vo.Schedule toVO() {
            return sopt.org.homepage.recruitment.domain.vo.Schedule.builder()
                    .applicationStartTime(applicationStartTime)
                    .applicationEndTime(applicationEndTime)
                    .applicationResultTime(applicationResultTime)
                    .interviewStartTime(interviewStartTime)
                    .interviewEndTime(interviewEndTime)
                    .finalResultTime(finalResultTime)
                    .build();
        }
    }
}