package sopt.org.homepage.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.review.domain.vo.*;
import sopt.org.homepage.review.exception.DuplicateReviewUrlException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 리뷰 도메인 엔티티
 *
 * 책임:
 * - 리뷰 생성 시 비즈니스 규칙 검증
 * - 리뷰 데이터의 일관성 유지
 */
@Entity
@Table(name = "\"Review\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Embedded
    private ReviewContent content;

    @Embedded
    private ReviewAuthor author;

    @Column(name = "\"generation\"", nullable = false)
    private Integer generation;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"part\"", nullable = false, length = 10)
    private Part part;

    @Embedded
    private ReviewCategory category;

    @Embedded
    private ReviewSubjects subjects;

    @Embedded
    private ReviewUrl url;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 리뷰 생성을 위한 팩토리 메서드
     *
     * 비즈니스 규칙:
     * 1. 모든 필드는 필수 (VO 내부에서 검증)
     * 2. 카테고리에 맞는 세부 주제가 있어야 함
     * 3. URL은 중복될 수 없음 (Repository에서 확인 필요)

     */
    public static Review create(
            ReviewContent content,
            ReviewAuthor author,
            Integer generation,
            Part part,
            ReviewCategory category,
            ReviewSubjects subjects,
            ReviewUrl url
    ) {
        // 1. 각 VO는 생성 시점에 이미 자가 검증됨

        // 2. 카테고리-세부주제 관계 검증 //기존 Service의 getReviewSubject() 로직이 여기로 이동
        subjects.validateForCategory(category);

        // 3. 엔티티 생성
        return new Review(content, author, generation, part, category, subjects, url);
    }

    /**
     * Private 생성자 - 팩토리 메서드를 통해서만 생성 가능
     */
    private Review(
            ReviewContent content,
            ReviewAuthor author,
            Integer generation,
            Part part,
            ReviewCategory category,
            ReviewSubjects subjects,
            ReviewUrl url
    ) {
        validateGeneration(generation);
        validatePart(part);

        this.content = content;
        this.author = author;
        this.generation = generation;
        this.part = part;
        this.category = category;
        this.subjects = subjects;
        this.url = url;
    }

    private void validateGeneration(Integer generation) {
        if (generation == null || generation <= 0) {
            throw new IllegalArgumentException("기수는 1 이상이어야 합니다.");
        }
    }

    private void validatePart(Part part) {
        if (part == null) {
            throw new IllegalArgumentException("파트는 필수입니다.");
        }
    }

    /**
     * URL 중복 검증을 위한 메서드
     * Service 계층에서 Repository 조회 후 호출
     */
    public void validateUrlUniqueness(boolean isDuplicate) {
        if (isDuplicate) {
            throw new DuplicateReviewUrlException("이미 등록된 활동후기입니다.");
        }
    }

    // === 조회용 편의 메서드 ===

    public String getTitle() {
        return content.getTitle();
    }

    public String getDescription() {
        return content.getDescription();
    }

    public String getThumbnailUrl() {
        return content.getThumbnailUrl();
    }

    public String getPlatform() {
        return content.getPlatform();
    }

    public String getAuthorName() {
        return author.getName();
    }

    public String getAuthorProfileImageUrl() {
        return author.getProfileImageUrl();
    }

    public String getCategoryValue() {
        return category.getValue();
    }
    /**
     * 카테고리 타입 조회
     */
    public CategoryType getCategoryType() {
        return category.getType();
    }

    /**
     * 카테고리 표시명 조회
     */
    public String getCategoryDisplayName() {
        return category.getType().getDisplayName();
    }


    public List<String> getSubjectValues() {
        return subjects.getValues();
    }

    public String getUrlValue() {
        return url.getValue();
    }




}