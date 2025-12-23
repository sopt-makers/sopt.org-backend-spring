package sopt.org.homepage.recruitment;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sopt.org.homepage.recruitment.vo.RecruitType;
import sopt.org.homepage.recruitment.vo.Schedule;

/**
 * Recruitment 애그리거트 루트
 * <p>
 * 책임: - 기수별 모집 일정 관리 - OB/YB 구분 - 모집 프로세스 일정 관리
 */
@Entity
@Table(name = "\"Recruitment\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"generationId\"", nullable = false)
    private Integer generationId;  // Generation FK

    @Enumerated(EnumType.STRING)
    @Column(name = "\"recruitType\"", nullable = false, length = 10)
    private RecruitType recruitType;  // OB, YB

    @Embedded
    private Schedule schedule;  // 모집 일정 (VO)

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Recruitment(
            Integer generationId,
            RecruitType recruitType,
            Schedule schedule
    ) {
        validateGenerationId(generationId);
        validateRecruitType(recruitType);
        validateSchedule(schedule);

        this.generationId = generationId;
        this.recruitType = recruitType;
        this.schedule = schedule;
    }

    // === 비즈니스 메서드 ===

    /**
     * 모집 일정 전체 수정
     */
    public void updateSchedule(Schedule schedule) {
        validateSchedule(schedule);
        this.schedule = schedule;
    }

    /**
     * 지원 기간만 수정
     */
    public void updateApplicationPeriod(String startTime, String endTime) {
        this.schedule = this.schedule.updateApplicationPeriod(startTime, endTime);
    }

    /**
     * 면접 기간만 수정
     */
    public void updateInterviewPeriod(String startTime, String endTime) {
        this.schedule = this.schedule.updateInterviewPeriod(startTime, endTime);
    }

    /**
     * 모집 중인지 확인
     */
    public boolean isRecruitingNow() {
        return schedule.isRecruitingNow();
    }

    /**
     * 지원 기간인지 확인
     */
    public boolean isApplicationPeriod() {
        return schedule.isApplicationPeriod();
    }

    /**
     * 면접 기간인지 확인
     */
    public boolean isInterviewPeriod() {
        return schedule.isInterviewPeriod();
    }

    // === Validation ===

    private void validateGenerationId(Integer generationId) {
        if (generationId == null || generationId <= 0) {
            throw new IllegalArgumentException("Generation ID must be positive");
        }
    }

    private void validateRecruitType(RecruitType recruitType) {
        if (recruitType == null) {
            throw new IllegalArgumentException("Recruit type must not be null");
        }
    }

    private void validateSchedule(Schedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule must not be null");
        }
    }
}
