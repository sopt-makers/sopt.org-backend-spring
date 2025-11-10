package sopt.org.homepage.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.review.exception.InvalidReviewContentException;

/**
 * 리뷰 컨텐츠 Value Object
 *
 * 비즈니스 규칙:
 * - 제목은 필수이며 1~1000자
 * - 설명은 필수이며 1~2000자
 * - 플랫폼은 필수이며 1~50자
 * - 썸네일 URL은 선택사항
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class ReviewContent {

    @Column(name = "\"title\"", nullable = false, length = 1000)
    private String title;

    @Column(name = "\"description\"", nullable = false, length = 2000)
    private String description;

    @Column(name = "\"thumbnailUrl\"", nullable = true, length = 500)
    private String thumbnailUrl;

    @Column(name = "\"platform\"", nullable = false, length = 50)
    private String platform;

    public ReviewContent(String title, String description, String thumbnailUrl, String platform) {
        validateTitle(title);
        validateDescription(description);
        validatePlatform(platform);
        validateThumbnailUrl(thumbnailUrl);

        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.platform = platform;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidReviewContentException("리뷰 제목은 필수입니다.");
        }
        if (title.length() > 1000) {
            throw new InvalidReviewContentException("리뷰 제목은 1000자를 초과할 수 없습니다.");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidReviewContentException("리뷰 설명은 필수입니다.");
        }
        if (description.length() > 2000) {
            throw new InvalidReviewContentException("리뷰 설명은 2000자를 초과할 수 없습니다.");
        }
    }

    private void validatePlatform(String platform) {
        if (platform == null || platform.isBlank()) {
            throw new InvalidReviewContentException("플랫폼 정보는 필수입니다.");
        }
        if (platform.length() > 50) {
            throw new InvalidReviewContentException("플랫폼 정보는 50자를 초과할 수 없습니다.");
        }
    }

    private void validateThumbnailUrl(String thumbnailUrl) {
        // 썸네일은 선택사항이므로 null 허용
        if (thumbnailUrl != null && thumbnailUrl.length() > 500) {
            throw new InvalidReviewContentException("썸네일 URL은 500자를 초과할 수 없습니다.");
        }
    }
}