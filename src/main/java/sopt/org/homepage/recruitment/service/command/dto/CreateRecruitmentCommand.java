package sopt.org.homepage.recruitment.service.command.dto;

import lombok.Builder;
import sopt.org.homepage.recruitment.domain.Recruitment;
import sopt.org.homepage.recruitment.domain.vo.RecruitType;
import sopt.org.homepage.recruitment.domain.vo.Schedule;

/**
 * CreateRecruitmentCommand
 *
 * 모집 일정 생성 커맨드
 */
@Builder
public record CreateRecruitmentCommand(
        Integer generationId,
        RecruitType recruitType,
        ScheduleCommand schedule
) {
    public Recruitment toEntity() {
        return Recruitment.builder()
                .generationId(generationId)
                .recruitType(recruitType)
                .schedule(schedule.toVO())
                .build();
    }

    @Builder
    public record ScheduleCommand(
            String applicationStartTime,
            String applicationEndTime,
            String applicationResultTime,
            String interviewStartTime,
            String interviewEndTime,
            String finalResultTime
    ) {
        public Schedule toVO() {
            return Schedule.builder()
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