package sopt.org.homepage.generation;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sopt.org.homepage.generation.vo.BrandingColor;

/**
 * Generation (기수) 애그리거트 루트
 * <p>
 * 책임: - 기수 기본 정보 관리 (번호, 이름) - 브랜딩 설정 관리 (컬러, 버튼) - 이미지 리소스 관리 (헤더 이미지)
 */
@Entity
@Table(name = "generation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Generation {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;  // generation number (35, 36, ...)

    @Column(name = "name", nullable = false, length = 50)
    private String name;  // "35기", "SOPT 35기"

    @Column(name = "header_image", nullable = false, length = 500)
    private String headerImage;  // Main 페이지 헤더 이미지 URL

    @Column(name = "recruit_header_image", nullable = false, length = 500)
    private String recruitHeaderImage;  // Recruiting 페이지 헤더 이미지 URL

    @Column(name = "home_header_image", nullable = false, length = 500)
    private String homeHeaderImage;  // Home 페이지 헤더 이미지 URL

    @Embedded
    private BrandingColor brandingColor;  // 브랜딩 컬러 (VO)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Generation(
            Integer id,
            String name,
            String headerImage,
            String recruitHeaderImage,
            String homeHeaderImage,
            BrandingColor brandingColor
    ) {
        this.id = id;
        this.name = name;
        this.headerImage = headerImage;
        this.recruitHeaderImage = recruitHeaderImage;
        this.homeHeaderImage = homeHeaderImage;
        this.brandingColor = brandingColor;
    }

    // === 비즈니스 메서드 ===

    /**
     * 기수 정보 업데이트
     */
    public void update(
            String name,
            String headerImage,
            String recruitHeaderImage,
            String homeHeaderImage,
            BrandingColor brandingColor
    ) {
        this.name = name;
        this.headerImage = headerImage;
        this.recruitHeaderImage = recruitHeaderImage;
        this.homeHeaderImage = homeHeaderImage;
        this.brandingColor = brandingColor;
    }

    /**
     * 헤더 이미지 변경
     */
    public void updateHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    /**
     * 모집 헤더 이미지 변경
     */
    public void updateRecruitHeaderImage(String recruitHeaderImage) {
        this.recruitHeaderImage = recruitHeaderImage;
    }

    /**
     * 홈 헤더 이미지 변경
     */
    public void updateHomeHeaderImage(String homeHeaderImage) {
        this.homeHeaderImage = homeHeaderImage;
    }

    /**
     * 브랜딩 컬러 변경
     */
    public void updateBrandingColor(BrandingColor brandingColor) {
        this.brandingColor = brandingColor;
    }

}
