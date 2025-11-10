package sopt.org.homepage.recruitment.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PartIntroduction Value Object
 *
 * 파트별 소개 정보
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartIntroduction {

    @Column(name = "\"introductionContent\"", nullable = false, length = 2000)
    private String content;  // 파트 소개 내용

    @Column(name = "\"introductionPreference\"", nullable = false, length = 1000)
    private String preference;  // 선호하는 지원자 특성

    @Builder
    public PartIntroduction(String content, String preference) {
        validateContent(content);
        validatePreference(preference);

        this.content = content;
        this.preference = preference;
    }

    /**
     * 소개 내용 수정
     */
    public PartIntroduction updateContent(String content) {
        validateContent(content);
        return PartIntroduction.builder()
                .content(content)
                .preference(this.preference)
                .build();
    }

    /**
     * 선호사항 수정
     */
    public PartIntroduction updatePreference(String preference) {
        validatePreference(preference);
        return PartIntroduction.builder()
                .content(this.content)
                .preference(preference)
                .build();
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Introduction content must not be blank");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("Introduction content must be less than 2000 characters");
        }
    }

    private void validatePreference(String preference) {
        if (preference == null || preference.isBlank()) {
            throw new IllegalArgumentException("Introduction preference must not be blank");
        }
        if (preference.length() > 1000) {
            throw new IllegalArgumentException("Introduction preference must be less than 1000 characters");
        }
    }
}