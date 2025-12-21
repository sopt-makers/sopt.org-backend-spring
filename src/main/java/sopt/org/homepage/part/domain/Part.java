package sopt.org.homepage.part.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import sopt.org.homepage.global.common.type.PartType;

/**
 * PartType 애그리거트 루트
 * <p>
 * 책임: - SOPT 파트 정보 관리 - 파트별 소개글 관리 - 파트별 커리큘럼 관리
 */
@Entity
@Table(name = "\"PartType\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"generationId\"", nullable = false)
    private Integer generationId;  // Generation FK

    @Enumerated(EnumType.STRING)
    @Column(name = "\"partType\"", nullable = false, length = 20)
    private PartType partType;  // ANDROID, IOS, WEB, SERVER, PLAN, DESIGN

    @Column(name = "\"description\"", nullable = false, length = 1000)
    private String description;  // 파트 소개글 (Main 페이지용)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"curriculums\"", nullable = false, columnDefinition = "text")
    private List<String> curriculums = new ArrayList<>();  // 주차별 커리큘럼 (About 페이지용)

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Part(
            Integer generationId,
            PartType partType,
            String description,
            List<String> curriculums
    ) {
        validateGenerationId(generationId);
        validatePartType(partType);
        validateDescription(description);
        validateCurriculums(curriculums);

        this.generationId = generationId;
        this.partType = partType;
        this.description = description;
        this.curriculums = curriculums != null ? new ArrayList<>(curriculums) : new ArrayList<>();
    }

    // === 비즈니스 메서드 ===

    /**
     * 파트 정보 전체 수정
     */
    public void update(String description, List<String> curriculums) {
        validateDescription(description);
        validateCurriculums(curriculums);

        this.description = description;
        this.curriculums = curriculums != null ? new ArrayList<>(curriculums) : new ArrayList<>();
    }

    /**
     * 소개글만 수정
     */
    public void updateDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    /**
     * 커리큘럼만 수정
     */
    public void updateCurriculums(List<String> curriculums) {
        validateCurriculums(curriculums);
        this.curriculums = curriculums != null ? new ArrayList<>(curriculums) : new ArrayList<>();
    }

    /**
     * 특정 주차 커리큘럼 수정
     */
    public void updateCurriculum(int week, String curriculum) {
        validateWeek(week);
        validateCurriculumContent(curriculum);

        if (week >= curriculums.size()) {
            throw new IllegalArgumentException(
                    String.format("Week %d does not exist. Current weeks: %d", week, curriculums.size())
            );
        }

        curriculums.set(week, curriculum);
    }

    /**
     * 커리큘럼 추가 (주차 추가)
     */
    public void addCurriculum(String curriculum) {
        validateCurriculumContent(curriculum);
        this.curriculums.add(curriculum);
    }

    /**
     * 특정 주차 커리큘럼 삭제
     */
    public void removeCurriculum(int week) {
        validateWeek(week);

        if (week >= curriculums.size()) {
            throw new IllegalArgumentException(
                    String.format("Week %d does not exist. Current weeks: %d", week, curriculums.size())
            );
        }

        this.curriculums.remove(week);
    }

    /**
     * 커리큘럼 주차 수 조회
     */
    public int getCurriculumWeekCount() {
        return this.curriculums.size();
    }

    // === Validation ===

    private void validateGenerationId(Integer generationId) {
        if (generationId == null || generationId <= 0) {
            throw new IllegalArgumentException("Generation ID must be positive");
        }
    }

    private void validatePartType(PartType partType) {
        if (partType == null) {
            throw new IllegalArgumentException("PartType type must not be null");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }
        if (description.length() > 1000) {
            throw new IllegalArgumentException("Description must be less than 1000 characters");
        }
    }

    private void validateCurriculums(List<String> curriculums) {
        if (curriculums == null) {
            return;  // null은 빈 리스트로 처리
        }

        for (String curriculum : curriculums) {
            validateCurriculumContent(curriculum);
        }
    }

    private void validateCurriculumContent(String curriculum) {
        if (curriculum == null || curriculum.isBlank()) {
            throw new IllegalArgumentException("Curriculum content must not be blank");
        }
        if (curriculum.length() > 200) {
            throw new IllegalArgumentException("Curriculum content must be less than 200 characters");
        }
    }

    private void validateWeek(int week) {
        if (week < 0) {
            throw new IllegalArgumentException("Week must be non-negative");
        }
    }
}
