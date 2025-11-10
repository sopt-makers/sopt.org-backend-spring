package sopt.org.homepage.soptstory.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * SoptStory 콘텐츠 Value Object
 *
 * 책임:
 * - 제목, 설명, 썸네일 URL의 묶음 관리
 * - 각 필드의 유효성 검증
 * - 콘텐츠 불변성 보장
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class SoptStoryContent {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 600;
    private static final int MAX_THUMBNAIL_URL_LENGTH = 500;

    @Column(name = "\"title\"", nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Column(name = "\"description\"", nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(name = "\"thumbnailUrl\"", nullable = true, length = MAX_THUMBNAIL_URL_LENGTH)
    private String thumbnailUrl;

    /**
     * 콘텐츠 생성
     *
     * @param title 제목
     * @param description 설명
     * @param thumbnailUrl 썸네일 URL (nullable)
     */
    public SoptStoryContent(String title, String description, String thumbnailUrl) {
        validateTitle(title);
        validateDescription(description);
        validateThumbnailUrl(thumbnailUrl);

        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("제목은 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH)
            );
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("설명은 필수입니다.");
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("설명은 %d자를 초과할 수 없습니다.", MAX_DESCRIPTION_LENGTH)
            );
        }
    }

    private void validateThumbnailUrl(String thumbnailUrl) {
        // thumbnailUrl은 nullable이므로 null 체크만
        if (thumbnailUrl != null && thumbnailUrl.length() > MAX_THUMBNAIL_URL_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("썸네일 URL은 %d자를 초과할 수 없습니다.", MAX_THUMBNAIL_URL_LENGTH)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoptStoryContent that = (SoptStoryContent) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(thumbnailUrl, that.thumbnailUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, thumbnailUrl);
    }
}