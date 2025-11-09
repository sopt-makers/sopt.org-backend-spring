package sopt.org.homepage.recruitment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.recruitment.domain.vo.PartIntroduction;

import java.time.LocalDateTime;

/**
 * RecruitPartIntroduction 애그리거트 루트
 *
 * 책임:
 * - 모집 시 파트별 소개 관리
 * - 파트별 선호 사항 관리
 */
@Entity
@Table(name = "\"RecruitPartIntroduction\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitPartIntroduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"generationId\"", nullable = false)
    private Integer generationId;  // Generation FK

    @Enumerated(EnumType.STRING)
    @Column(name = "\"part\"", nullable = false, length = 20)
    private PartType part;

    @Embedded
    private PartIntroduction introduction;  // 소개 및 선호사항 (VO)

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public RecruitPartIntroduction(
            Integer generationId,
            PartType part,
            PartIntroduction introduction
    ) {
        validateGenerationId(generationId);
        validatePart(part);
        validateIntroduction(introduction);

        this.generationId = generationId;
        this.part = part;
        this.introduction = introduction;
    }

    // === 비즈니스 메서드 ===

    /**
     * 소개 정보 수정
     */
    public void updateIntroduction(PartIntroduction introduction) {
        validateIntroduction(introduction);
        this.introduction = introduction;
    }

    /**
     * 소개 내용만 수정
     */
    public void updateContent(String content) {
        this.introduction = this.introduction.updateContent(content);
    }

    /**
     * 선호사항만 수정
     */
    public void updatePreference(String preference) {
        this.introduction = this.introduction.updatePreference(preference);
    }

    // === Validation ===

    private void validateGenerationId(Integer generationId) {
        if (generationId == null || generationId <= 0) {
            throw new IllegalArgumentException("Generation ID must be positive");
        }
    }

    private void validatePart(PartType part) {
        if (part == null) {
            throw new IllegalArgumentException("Part must not be null");
        }
    }

    private void validateIntroduction(PartIntroduction introduction) {
        if (introduction == null) {
            throw new IllegalArgumentException("Introduction must not be null");
        }
    }
}