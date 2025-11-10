package sopt.org.homepage.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.review.exception.InvalidReviewUrlException;

/**
 * 리뷰 URL Value Object
 *
 * 비즈니스 규칙:
 * - URL은 필수
 * - URL은 500자 이하
 * - 간단한 URL 형식 검증
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class ReviewUrl {

    @Column(name = "\"url\"", nullable = false, length = 500)
    private String value;

    public ReviewUrl(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidReviewUrlException("리뷰 URL은 필수입니다.");
        }
        if (value.length() > 500) {
            throw new InvalidReviewUrlException("리뷰 URL은 500자를 초과할 수 없습니다.");
        }
        // 기본적인 URL 형식 검증
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            throw new InvalidReviewUrlException("유효한 URL 형식이 아닙니다. (http:// 또는 https://로 시작해야 합니다)");
        }
    }

    public String getValue() {
        return value;
    }
}