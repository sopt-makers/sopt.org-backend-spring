package sopt.org.homepage.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.review.exception.InvalidReviewAuthorException;

/**
 * 리뷰 작성자 Value Object
 *
 * 비즈니스 규칙:
 * - 작성자명은 필수이며 1~20자
 * - 프로필 이미지 URL은 선택사항
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class ReviewAuthor {

    @Column(name = "\"author\"", nullable = false, length = 20)
    private String name;

    @Column(name = "\"authorProfileImageUrl\"", nullable = true, length = 500)
    private String profileImageUrl;

    public ReviewAuthor(String name, String profileImageUrl) {
        validateName(name);
        validateProfileImageUrl(profileImageUrl);

        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidReviewAuthorException("작성자명은 필수입니다.");
        }
        if (name.length() > 20) {
            throw new InvalidReviewAuthorException("작성자명은 20자를 초과할 수 없습니다.");
        }
    }

    private void validateProfileImageUrl(String profileImageUrl) {
        // 프로필 이미지는 선택사항
        if (profileImageUrl != null && profileImageUrl.length() > 500) {
            throw new InvalidReviewAuthorException("프로필 이미지 URL은 500자를 초과할 수 없습니다.");
        }
    }
}