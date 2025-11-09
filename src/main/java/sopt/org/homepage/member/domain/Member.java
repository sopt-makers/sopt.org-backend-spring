package sopt.org.homepage.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sopt.org.homepage.member.domain.vo.MemberRole;
import sopt.org.homepage.member.domain.vo.SnsLinks;

import java.time.LocalDateTime;

/**
 * Member 애그리거트 루트
 *
 * 책임:
 * - SOPT 운영진 정보 관리
 * - 기수별 운영진 구성
 * - SNS 링크 관리
 */
@Entity
@Table(name = "\"Member\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"generationId\"", nullable = false)
    private Integer generationId;  // Generation FK

    @Enumerated(EnumType.STRING)
    @Column(name = "\"role\"", nullable = false, length = 50)
    private MemberRole role;  // 회장, 부회장, 운영팀장, ...

    @Column(name = "\"name\"", nullable = false, length = 50)
    private String name;  // 이름

    @Column(name = "\"affiliation\"", nullable = false, length = 100)
    private String affiliation;  // 소속 (학교/회사)

    @Column(name = "\"introduction\"", nullable = false, length = 500)
    private String introduction;  // 한 줄 소개

    @Column(name = "\"profileImageUrl\"", nullable = false, length = 500)
    private String profileImageUrl;  // 프로필 이미지 URL

    @Embedded
    private SnsLinks snsLinks;  // SNS 링크 (VO)

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Member(
            Integer generationId,
            MemberRole role,
            String name,
            String affiliation,
            String introduction,
            String profileImageUrl,
            SnsLinks snsLinks
    ) {
        validateGenerationId(generationId);
        validateRole(role);
        validateName(name);
        validateAffiliation(affiliation);
        validateIntroduction(introduction);
        validateProfileImageUrl(profileImageUrl);

        this.generationId = generationId;
        this.role = role;
        this.name = name;
        this.affiliation = affiliation;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
        this.snsLinks = snsLinks != null ? snsLinks : SnsLinks.empty();
    }

    // === 비즈니스 메서드 ===

    /**
     * 운영진 정보 전체 수정
     */
    public void update(
            MemberRole role,
            String name,
            String affiliation,
            String introduction,
            String profileImageUrl,
            SnsLinks snsLinks
    ) {
        validateRole(role);
        validateName(name);
        validateAffiliation(affiliation);
        validateIntroduction(introduction);
        validateProfileImageUrl(profileImageUrl);

        this.role = role;
        this.name = name;
        this.affiliation = affiliation;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
        this.snsLinks = snsLinks != null ? snsLinks : SnsLinks.empty();
    }

    /**
     * 프로필 이미지만 변경
     */
    public void updateProfileImage(String profileImageUrl) {
        validateProfileImageUrl(profileImageUrl);
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 소개글만 변경
     */
    public void updateIntroduction(String introduction) {
        validateIntroduction(introduction);
        this.introduction = introduction;
    }

    /**
     * SNS 링크만 변경
     */
    public void updateSnsLinks(SnsLinks snsLinks) {
        this.snsLinks = snsLinks != null ? snsLinks : SnsLinks.empty();
    }

    /**
     * 역할 변경
     */
    public void updateRole(MemberRole role) {
        validateRole(role);
        this.role = role;
    }

    // === Validation ===

    private void validateGenerationId(Integer generationId) {
        if (generationId == null || generationId <= 0) {
            throw new IllegalArgumentException("Generation ID must be positive");
        }
    }

    private void validateRole(MemberRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Member role must not be null");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name must not be blank");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Member name must be less than 50 characters");
        }
    }

    private void validateAffiliation(String affiliation) {
        if (affiliation == null || affiliation.isBlank()) {
            throw new IllegalArgumentException("Affiliation must not be blank");
        }
        if (affiliation.length() > 100) {
            throw new IllegalArgumentException("Affiliation must be less than 100 characters");
        }
    }

    private void validateIntroduction(String introduction) {
        if (introduction == null || introduction.isBlank()) {
            throw new IllegalArgumentException("Introduction must not be blank");
        }
        if (introduction.length() > 500) {
            throw new IllegalArgumentException("Introduction must be less than 500 characters");
        }
    }

    private void validateProfileImageUrl(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            throw new IllegalArgumentException("Profile image URL must not be blank");
        }
    }
}