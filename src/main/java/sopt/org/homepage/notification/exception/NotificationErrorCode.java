package sopt.org.homepage.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Notification 도메인 에러 코드
 * <p>
 * 장점: - 모든 에러를 한 곳에서 관리 - 에러 코드, 메시지, HTTP 상태 코드를 함께 관리 - 새로운 에러 추가 시 enum만 추가
 */
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode {

    // ===== Email 관련 에러 =====
    INVALID_EMAIL_FORMAT(
            "유효하지 않은 이메일 형식입니다: %s",
            HttpStatus.BAD_REQUEST
    ),

    // ===== Generation 관련 에러 =====

    INVALID_GENERATION_NOT_POSITIVE(
            "기수는 양수여야 합니다: %d",
            HttpStatus.BAD_REQUEST
    ),

    // ===== Notification 관련 에러 =====

    DUPLICATE_NOTIFICATION(
            "이미 등록된 알림입니다. (이메일: %s, 기수: %d)",
            HttpStatus.CONFLICT  // ✅ 409
    );


    private final String messageTemplate;
    private final HttpStatus httpStatus;

    /**
     * 파라미터 없이 메시지 생성
     */
    public String getMessage() {
        return messageTemplate;
    }

    /**
     * 파라미터를 포함한 메시지 생성
     */
    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
