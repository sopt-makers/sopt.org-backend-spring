package sopt.org.homepage.review.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Review 도메인 전용 예외
 * <p>
 * 특징: - ErrorCode enum으로 에러 관리 - args 배열로 메시지 포맷팅 데이터 보관 - 다국어 지원 가능 - 로깅/분석 용이
 * <p>
 * 사용: throw ReviewDomainException.invalidUrlFormat("invalid-url");
 */
@Getter
public class ReviewDomainException extends RuntimeException {

    private final ReviewErrorCode errorCode;
    private final Object[] args;  // 메시지 포맷팅용 인자

    /**
     * 파라미터 없는 예외 생성
     */
    private ReviewDomainException(ReviewErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    /**
     * 파라미터 있는 예외 생성
     */
    private ReviewDomainException(ReviewErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * HTTP 상태 코드 반환
     */
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    // ===== URL 관련 팩토리 메서드 =====

    public static ReviewDomainException invalidUrlFormat(String url) {
        return new ReviewDomainException(
                ReviewErrorCode.INVALID_URL_FORMAT,
                url
        );
    }

    public static ReviewDomainException urlRequired() {
        return new ReviewDomainException(ReviewErrorCode.URL_REQUIRED);
    }

    public static ReviewDomainException duplicateUrl(String url) {
        return new ReviewDomainException(
                ReviewErrorCode.DUPLICATE_URL,
                url
        );
    }

    // ===== 카테고리 관련 팩토리 메서드 =====

    public static ReviewDomainException invalidCategory(String category) {
        return new ReviewDomainException(
                ReviewErrorCode.INVALID_CATEGORY,
                category
        );
    }

    public static ReviewDomainException categoryRequired() {
        return new ReviewDomainException(ReviewErrorCode.CATEGORY_REQUIRED);
    }

    // ===== 세부 주제 관련 팩토리 메서드 =====

    public static ReviewDomainException invalidSubjectsForCategory(
            String category,
            String subjects
    ) {
        return new ReviewDomainException(
                ReviewErrorCode.INVALID_SUBJECTS_FOR_CATEGORY,
                category,
                subjects
        );
    }

    public static ReviewDomainException subjectsRequiredForActivity() {
        return new ReviewDomainException(
                ReviewErrorCode.SUBJECTS_REQUIRED_FOR_ACTIVITY
        );
    }

    public static ReviewDomainException subjectsNotAllowed(String category) {
        return new ReviewDomainException(
                ReviewErrorCode.SUBJECTS_NOT_ALLOWED,
                category
        );
    }

    public static ReviewDomainException multipleSubjectsNotAllowed(String category) {
        return new ReviewDomainException(
                ReviewErrorCode.MULTIPLE_SUBJECTS_NOT_ALLOWED,
                category
        );
    }

    // ===== 작성자 관련 팩토리 메서드 =====

    public static ReviewDomainException authorNameRequired() {
        return new ReviewDomainException(ReviewErrorCode.AUTHOR_NAME_REQUIRED);
    }

    public static ReviewDomainException authorNameTooLong(int length) {
        return new ReviewDomainException(
                ReviewErrorCode.AUTHOR_NAME_TOO_LONG,
                length
        );
    }

    // ===== 컨텐츠 관련 팩토리 메서드 =====

    public static ReviewDomainException titleRequired() {
        return new ReviewDomainException(ReviewErrorCode.TITLE_REQUIRED);
    }

    public static ReviewDomainException titleTooLong(int length) {
        return new ReviewDomainException(
                ReviewErrorCode.TITLE_TOO_LONG,
                length
        );
    }

    public static ReviewDomainException descriptionRequired() {
        return new ReviewDomainException(ReviewErrorCode.DESCRIPTION_REQUIRED);
    }

    public static ReviewDomainException descriptionTooLong(int length) {
        return new ReviewDomainException(
                ReviewErrorCode.DESCRIPTION_TOO_LONG,
                length
        );
    }

    public static ReviewDomainException thumbnailUrlRequired() {
        return new ReviewDomainException(ReviewErrorCode.THUMBNAIL_URL_REQUIRED);
    }

    public static ReviewDomainException platformRequired() {
        return new ReviewDomainException(ReviewErrorCode.PLATFORM_REQUIRED);
    }

    // ===== 기수/파트 관련 팩토리 메서드 =====

    public static ReviewDomainException generationRequired() {
        return new ReviewDomainException(ReviewErrorCode.GENERATION_REQUIRED);
    }

    public static ReviewDomainException generationNotPositive(Integer generation) {
        return new ReviewDomainException(
                ReviewErrorCode.GENERATION_NOT_POSITIVE,
                generation
        );
    }

    public static ReviewDomainException partRequired() {
        return new ReviewDomainException(ReviewErrorCode.PART_REQUIRED);
    }
}
