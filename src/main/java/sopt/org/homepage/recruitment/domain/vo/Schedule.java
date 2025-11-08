package sopt.org.homepage.recruitment.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Schedule Value Object
 *
 * 모집 일정 정보
 * - 레거시 형식(ISO 8601: yyyy-MM-ddTHH:mm) 그대로 유지
 * - API 호환성 보장
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    // 레거시 형식 유지 (ISO 8601, 초 단위 없음)
    private static final DateTimeFormatter LEGACY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // 파싱용 (여러 형식 지원하되, 저장은 레거시 형식으로)
    private static final DateTimeFormatter[] PARSE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),   // 2025-08-16T10:00 (레거시)
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"), // 2025-08-16T10:00:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),  // 2025-08-16 10:00:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),     // 2025-08-16 10:00
            DateTimeFormatter.ISO_LOCAL_DATE_TIME                 // 다양한 ISO 형식
    };

    @Column(name = "\"applicationStartTime\"", nullable = false, length = 50)
    private String applicationStartTime;

    @Column(name = "\"applicationEndTime\"", nullable = false, length = 50)
    private String applicationEndTime;

    @Column(name = "\"applicationResultTime\"", nullable = false, length = 50)
    private String applicationResultTime;

    @Column(name = "\"interviewStartTime\"", nullable = false, length = 50)
    private String interviewStartTime;

    @Column(name = "\"interviewEndTime\"", nullable = false, length = 50)
    private String interviewEndTime;

    @Column(name = "\"finalResultTime\"", nullable = false, length = 50)
    private String finalResultTime;

    @Builder
    public Schedule(
            String applicationStartTime,
            String applicationEndTime,
            String applicationResultTime,
            String interviewStartTime,
            String interviewEndTime,
            String finalResultTime
    ) {
        // 어떤 형식이든 받아서 → 레거시 형식(yyyy-MM-ddTHH:mm)으로 저장
        this.applicationStartTime = normalizeToLegacyFormat(applicationStartTime, "Application start time");
        this.applicationEndTime = normalizeToLegacyFormat(applicationEndTime, "Application end time");
        this.applicationResultTime = normalizeToLegacyFormat(applicationResultTime, "Application result time");
        this.interviewStartTime = normalizeToLegacyFormat(interviewStartTime, "Interview start time");
        this.interviewEndTime = normalizeToLegacyFormat(interviewEndTime, "Interview end time");
        this.finalResultTime = normalizeToLegacyFormat(finalResultTime, "Final result time");
    }

    /**
     * 지원 기간 수정
     */
    public Schedule updateApplicationPeriod(String startTime, String endTime) {
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

    /**
     * 여러 형식을 받아서 → 레거시 형식(yyyy-MM-ddTHH:mm)으로 정규화
     *
     * 입력 형식 (모두 허용):
     * - 2025-08-16T10:00 (레거시) ✅
     * - 2025-08-16T10:00:00 (초 포함) ✅
     * - 2025-08-16 10:00:00 (공백 구분) ✅
     * - 2025-08-16 10:00 (공백, 초 없음) ✅
     *
     * 출력 형식 (항상 레거시):
     * - 2025-08-16T10:00
     */
    private String normalizeToLegacyFormat(String dateTime, String fieldName) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        // 이미 레거시 형식이면 그대로 반환 (검증만)
        try {
            LocalDateTime.parse(dateTime, LEGACY_FORMATTER);
            return dateTime;  // 이미 레거시 형식 (yyyy-MM-ddTHH:mm)
        } catch (DateTimeParseException ignored) {
            // 다른 형식이므로 변환 필요
        }

        // 여러 형식으로 파싱 후 → 레거시 형식으로 변환
        LocalDateTime parsed = parseFlexibleDateTime(dateTime, fieldName);
        return parsed.format(LEGACY_FORMATTER);  // yyyy-MM-ddTHH:mm
    }

    /**
     * 여러 형식을 지원하는 유연한 파싱
     */
    private LocalDateTime parseFlexibleDateTime(String dateTime, String fieldName) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        // 정의된 형식들로 파싱 시도
        for (DateTimeFormatter formatter : PARSE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTime, formatter);
            } catch (DateTimeParseException ignored) {
                // 다음 형식 시도
            }
        }

        // 최후의 시도: 초 단위 없는 ISO 형식
        try {
            if (dateTime.contains("T") && dateTime.length() == 16) {  // yyyy-MM-ddTHH:mm
                return LocalDateTime.parse(dateTime + ":00");  // 초 추가하여 파싱
            }
        } catch (DateTimeParseException ignored) {
            // 무시
        }

        // 모든 형식 실패
        throw new IllegalArgumentException(
                String.format(
                        "%s must be a valid datetime string. " +
                                "Supported formats: 'yyyy-MM-ddTHH:mm', 'yyyy-MM-dd HH:mm:ss', etc. " +
                                "Got: %s",
                        fieldName,
                        dateTime
                )
        );
    }

    /**
     * 저장된 문자열을 LocalDateTime으로 파싱 (레거시 형식 기준)
     */
    private LocalDateTime parseDateTime(String dateTime) {
        // 레거시 형식은 초 단위가 없으므로 :00 추가
        try {
            return LocalDateTime.parse(dateTime, LEGACY_FORMATTER);
        } catch (DateTimeParseException e) {
            // 혹시 다른 형식이 저장되어 있다면 유연하게 파싱
            return parseFlexibleDateTime(dateTime, "Stored datetime");
        }
    }
}
