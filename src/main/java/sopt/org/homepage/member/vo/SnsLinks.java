package sopt.org.homepage.member.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SnsLinks Value Object
 * <p>
 * 운영진 SNS 링크 정보
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnsLinks {

    @Column(name = "\"snsEmail\"", length = 100)
    private String email;

    @Column(name = "\"snsLinkedin\"", length = 200)
    private String linkedin;

    @Column(name = "\"snsGithub\"", length = 200)
    private String github;

    @Column(name = "\"snsBehance\"", length = 200)
    private String behance;

    @Builder
    public SnsLinks(String email, String linkedin, String github, String behance) {
        this.email = validateAndNormalize(email);
        this.linkedin = validateAndNormalize(linkedin);
        this.github = validateAndNormalize(github);
        this.behance = validateAndNormalize(behance);
    }

    /**
     * 빈 SNS 링크 생성 (모두 null)
     */
    public static SnsLinks empty() {
        return SnsLinks.builder().build();
    }

    /**
     * SNS 링크 유효성 검증 및 정규화 - null 또는 빈 문자열은 null로 변환 - 공백 제거
     */
    private String validateAndNormalize(String url) {
        if (url == null || url.isBlank()) {
            return "";  // ⭐ null → 빈 문자열
        }
        return url.trim();
    }

    /**
     * 모든 SNS 링크가 비어있는지 확인
     */
    public boolean isEmpty() {
        return email == null && linkedin == null && github == null && behance == null;
    }

    /**
     * 이메일 존재 여부
     */
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    /**
     * LinkedIn 존재 여부
     */
    public boolean hasLinkedin() {
        return linkedin != null && !linkedin.isBlank();
    }

    /**
     * GitHub 존재 여부
     */
    public boolean hasGithub() {
        return github != null && !github.isBlank();
    }

    /**
     * Behance 존재 여부
     */
    public boolean hasBehance() {
        return behance != null && !behance.isBlank();
    }
}
