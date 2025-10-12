package sopt.org.homepage.recruitment.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Schedule Value Object
 *
 * 모집 일정 정보
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Column(name = "\"applicationStartTime\"", nullable = false, length = 50)
    private String applicationStartTime;  // 지원 시작

    @Column(name = "\"applicationEndTime\"", nullable = false, length = 50)
    private String applicationEndTime;  // 지원 종료

    @Column(name = "\"applicationResultTime\"", nullable = false, length = 50)
    private String applicationResultTime;  // 지원 결과 발표

    @Column(name = "\"interviewStartTime\"", nullable = false, length = 50)
    private String interviewStartTime;  // 면접 시작

    @Column(name = "\"interviewEndTime\"", nullable = false, length = 50)
    private String interviewEndTime;  // 면접 종료

    @Column(name = "\"finalResultTime\"", nullable = false, length = 50)
    private String finalResultTime;  // 최종 합격 발표

    @Builder
    public Schedule(
            String applicationStartTime,
            String applicationEndTime,
            String applicationResultTime,
            String interviewStartTime,
            String interviewEndTime,
            String finalResultTime
    ) {
        validateDateTime(applicationStartTime, "Application start time");
        validateDateTime(applicationEndTime, "Application end time");
        validateDateTime(applicationResultTime, "Application result time");
        validateDateTime(interviewStartTime, "Interview start time");
        validateDateTime(interviewEndTime, "Interview end time");
        validateDateTime(finalResultTime, "Final result time");

        this.applicationStartTime = applicationStartTime;
        this.applicationEndTime = applicationEndTime;
        this.applicationResultTime = applicationResultTime;
        this.interviewStartTime = interviewStartTime;
        this.interviewEndTime = interviewEndTime;
        this.finalResultTime = finalResultTime;
    }

    /**
     * 지원 기간 수정
     */
    public Schedule updateApplicationPeriod(String startTime, String endTime) {
        validateDateTime(startTime, "Application start time");
        validateDateTime(endTime, "Application end time");

        return Schedule.builder()
                .applicationStartTime(startTime)
                .applicationEndTime(endTime)
                .applicationResultTime(this.applicationResultTime)
                .interviewStartTime(this.interviewStartTime)
                .interviewEndTime(this.interviewEndTime)
                .finalResultTime(this.finalResultTime)
                .build();
    }

    /**
     * 면접 기간 수정
     */
    public Schedule updateInterviewPeriod(String startTime, String endTime) {
        validateDateTime(startTime, "Interview start time");
        validateDateTime(endTime, "Interview end time");

        return Schedule.builder()
                .applicationStartTime(this.applicationStartTime)
                .applicationEndTime(this.applicationEndTime)
                .applicationResultTime(this.applicationResultTime)
                .interviewStartTime(startTime)
                .interviewEndTime(endTime)
                .finalResultTime(this.finalResultTime)
                .build();
    }

    /**
     * 현재 모집 중인지 확인
     */
    public boolean isRecruitingNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = parseDateTime(applicationStartTime);
        LocalDateTime end = parseDateTime(finalResultTime);

        return !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * 지원 기간인지 확인
     */
    public boolean isApplicationPeriod() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = parseDateTime(applicationStartTime);
        LocalDateTime end = parseDateTime(applicationEndTime);

        return !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * 면접 기간인지 확인
     */
    public boolean isInterviewPeriod() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = parseDateTime(interviewStartTime);
        LocalDateTime end = parseDateTime(interviewEndTime);

        return !now.isBefore(start) && !now.isAfter(end);
    }

    // === Private Methods ===

    private void validateDateTime(String dateTime, String fieldName) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        try {
            LocalDateTime.parse(dateTime, FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    fieldName + " must be in format 'yyyy-MM-dd HH:mm:ss'. Got: " + dateTime
            );
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, FORMATTER);
    }
}