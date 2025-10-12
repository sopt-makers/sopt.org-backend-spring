package sopt.org.homepage.corevalue.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * CoreValue 애그리거트 루트
 *
 * 책임:
 * - SOPT 핵심 가치 관리
 * - 기수별 핵심 가치 정의
 * - 순서(displayOrder) 관리
 */
@Entity
@Table(name = "\"CoreValue\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoreValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"generationId\"", nullable = false)
    private Integer generationId;  // Generation FK

    @Column(name = "\"value\"", nullable = false, length = 50)
    private String value;  // 핵심 가치 제목 (예: "도전", "성장", "협력")

    @Column(name = "\"description\"", nullable = false, length = 500)
    private String description;  // 핵심 가치 설명

    @Column(name = "\"imageUrl\"", nullable = false, length = 500)
    private String imageUrl;  // 이미지 URL

    @Column(name = "\"displayOrder\"", nullable = false)
    private Integer displayOrder;  // 표시 순서 (1, 2, 3, ...)

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CoreValue(
            Integer generationId,
            String value,
            String description,
            String imageUrl,
            Integer displayOrder
    ) {
        validateGenerationId(generationId);
        validateValue(value);
        validateDescription(description);
        validateImageUrl(imageUrl);
        validateDisplayOrder(displayOrder);

        this.generationId = generationId;
        this.value = value;
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    // === 비즈니스 메서드 ===

    /**
     * 핵심 가치 정보 수정
     */
    public void update(String value, String description, String imageUrl, Integer displayOrder) {
        validateValue(value);
        validateDescription(description);
        validateImageUrl(imageUrl);
        validateDisplayOrder(displayOrder);

        this.value = value;
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    /**
     * 이미지만 변경
     */
    public void updateImage(String imageUrl) {
        validateImageUrl(imageUrl);
        this.imageUrl = imageUrl;
    }

    /**
     * 순서 변경
     */
    public void updateDisplayOrder(Integer displayOrder) {
        validateDisplayOrder(displayOrder);
        this.displayOrder = displayOrder;
    }

    // === Validation ===

    private void validateGenerationId(Integer generationId) {
        if (generationId == null || generationId <= 0) {
            throw new IllegalArgumentException("Generation ID must be positive");
        }
    }

    private void validateValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Core value must not be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Core value must be less than 50 characters");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }
        if (description.length() > 500) {
            throw new IllegalArgumentException("Description must be less than 500 characters");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Image URL must not be blank");
        }
    }

    private void validateDisplayOrder(Integer displayOrder) {
        if (displayOrder == null || displayOrder < 0) {
            throw new IllegalArgumentException("Display order must be non-negative");
        }
    }
}