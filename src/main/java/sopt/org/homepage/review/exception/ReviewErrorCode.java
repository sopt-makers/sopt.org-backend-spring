package sopt.org.homepage.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Review 도메인 에러 코드
 * <p>
 * 특징: - 메시지 템플릿 사용 (동적 포맷팅) - HTTP 상태 코드 포함 - 다국어 지원 가능
 * <p>
 * 명명 규칙: - INVALID_* : 입력값 검증 실패 (400) - DUPLICATE_* : 중복 (409) - *_REQUIRED : 필수값 누락 (400) - *_TOO_LONG : 길이 초과 (400)
 */
@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {

    // ===== URL 관련 =====
    INVALID_URL_FORMAT(
            "유효하지 않은 URL 형식입니다: %s",
            HttpStatus.BAD_REQUEST
    ),
    URL_REQUIRED(
            "URL은 필수입니다",
            HttpStatus.BAD_REQUEST
    ),
    DUPLICATE_URL(
            "이미 등록된 URL입니다: %s",
            HttpStatus.CONFLICT
    ),

    // ===== 카테고리 관련 =====
    INVALID_CATEGORY(
            "유효하지 않은 카테고리입니다: %s",
            HttpStatus.BAD_REQUEST
    ),
    CATEGORY_REQUIRED(
            "카테고리는 필수입니다",
            HttpStatus.BAD_REQUEST
    ),

    // ===== 세부 주제 관련 =====
    INVALID_SUBJECTS_FOR_CATEGORY(
            "해당 카테고리에 적합하지 않은 세부 주제입니다. (카테고리: %s, 세부주제: %s)",
            HttpStatus.BAD_REQUEST
    ),
    SUBJECTS_REQUIRED_FOR_ACTIVITY(
            "전체 활동 카테고리는 세부 주제가 필요합니다",
            HttpStatus.BAD_REQUEST
    ),
    SUBJECTS_NOT_ALLOWED(
            "해당 카테고리는 세부 주제를 가질 수 없습니다: %s",
            HttpStatus.BAD_REQUEST
    ),
    MULTIPLE_SUBJECTS_NOT_ALLOWED(
            "해당 카테고리는 하나의 세부 주제만 가질 수 있습니다: %s",
            HttpStatus.BAD_REQUEST
    ),

    // ===== 작성자 관련 =====
    AUTHOR_NAME_REQUIRED(
            "작성자명은 필수입니다",
            HttpStatus.BAD_REQUEST
    ),
    AUTHOR_NAME_TOO_LONG(
            "작성자명은 50자를 초과할 수 없습니다. (현재 길이: %d)",
            HttpStatus.BAD_REQUEST
    ),

    // ===== 컨텐츠 관련 =====
    TITLE_REQUIRED(
            "제목은 필수입니다",
            HttpStatus.BAD_REQUEST
    ),
    TITLE_TOO_LONG(
            "제목은 200자를 초과할 수 없습니다. (현재 길이: %d)",
            HttpStatus.BAD_REQUEST
    ),
    DESCRIPTION_REQUIRED(
            "설명은 필수입니다",
            HttpStatus.BAD_REQUEST
    ),
    DESCRIPTION_TOO_LONG(
            "설명은 500자를 초과할 수 없습니다. (현재 길이: %d)",
            HttpStatus.BAD_REQUEST
    ),
    THUMBNAIL_URL_REQUIRED(
            "썸네일 URL은 필수입니다",
            HttpStatus.BAD_REQUEST
    ),
    PLATFORM_REQUIRED(
            "플랫폼은 필수입니다",
            HttpStatus.BAD_REQUEST
    ),

    // ===== 기수/파트 관련 =====
    GENERATION_REQUIRED(
            "기수는 필수입니다",
            HttpStatus.BAD_REQUEST
    ),
    GENERATION_NOT_POSITIVE(
            "기수는 1 이상이어야 합니다: %d",
            HttpStatus.BAD_REQUEST
    ),
    PART_REQUIRED(
            "파트는 필수입니다",
            HttpStatus.BAD_REQUEST
    );

    private final String messageTemplate;
    private final HttpStatus httpStatus;

    /**
     * 파라미터 없는 메시지 반환
     */
    public String getMessage() {
        return messageTemplate;
    }

    /**
     * 파라미터로 포맷팅된 메시지 반환
     *
     * @param args 메시지 템플릿에 들어갈 파라미터
     * @return 포맷팅된 메시지
     */
    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
