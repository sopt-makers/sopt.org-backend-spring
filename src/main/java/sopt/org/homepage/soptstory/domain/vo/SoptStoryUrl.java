package sopt.org.homepage.soptstory.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * SoptStory URL Value Object
 *
 * 책임:
 * - URL 형식 검증
 * - URL 불변성 보장
 * - 중복 URL 방지를 위한 동등성 비교
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class SoptStoryUrl {

    private static final String URL_REGEX =
            "^(https?://)([\\w.-]+)(:\\d+)?(/[\\w./-]*)?$";

    @Column(name = "\"soptStoryUrl\"", nullable = false, length = 500)
    private String value;

    /**
     * URL 생성
     *
     * @param value URL 문자열
     * @throws IllegalArgumentException URL이 null이거나 형식이 잘못된 경우
     */
    public SoptStoryUrl(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("URL은 필수입니다.");
        }

        if (value.length() > 500) {
            throw new IllegalArgumentException("URL은 500자를 초과할 수 없습니다.");
        }

        if (!value.matches(URL_REGEX)) {
            throw new IllegalArgumentException("올바른 URL 형식이 아닙니다: " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoptStoryUrl that = (SoptStoryUrl) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}