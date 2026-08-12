package sopt.org.homepage.recruitment.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Schedule VO 테스트")
class ScheduleTest {

    @Test
    @DisplayName("✅ 선택 일정이 없으면 모집 중 상태는 false")
    void isRecruitingNow_ReturnsFalse_WhenFinalResultTimeIsNull() {
        // given
        Schedule schedule = Schedule.builder()
                .applicationStartTime("2026-01-01 00:00:00")
                .applicationEndTime("2026-01-31 23:59:59")
                .build();

        // when & then
        assertThatCode(schedule::isRecruitingNow).doesNotThrowAnyException();
        assertThat(schedule.isRecruitingNow()).isFalse();
    }

    @Test
    @DisplayName("✅ 선택 일정이 없으면 면접 기간 상태는 false")
    void isInterviewPeriod_ReturnsFalse_WhenInterviewTimesAreNull() {
        // given
        Schedule schedule = Schedule.builder()
                .applicationStartTime("2026-01-01 00:00:00")
                .applicationEndTime("2026-01-31 23:59:59")
                .build();

        // when & then
        assertThatCode(schedule::isInterviewPeriod).doesNotThrowAnyException();
        assertThat(schedule.isInterviewPeriod()).isFalse();
    }
}
