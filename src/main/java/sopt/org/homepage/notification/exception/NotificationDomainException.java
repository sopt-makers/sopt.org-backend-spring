package sopt.org.homepage.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Notification 도메인 예외
 * <p>
 * 특징: - 모든 도메인 예외를 하나의 클래스로 표현 - ErrorCode Enum으로 구분 - HTTP 상태 코드를 Enum에서 관리
 */
@Getter
public class NotificationDomainException extends RuntimeException {

    private final NotificationErrorCode errorCode;
    private final Object[] args;  // 메시지 포맷팅용 인자

    /**
     * 파라미터 없는 예외 생성
     */
    private NotificationDomainException(NotificationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    /**
     * 파라미터 있는 예외 생성
     */
    private NotificationDomainException(NotificationErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    // ===== 정적 팩토리 메서드 =====

    // Email 관련
    public static NotificationDomainException emailInvalidFormat(String email) {
        return new NotificationDomainException(
                NotificationErrorCode.INVALID_EMAIL_FORMAT,
                email
        );
    }

    public static NotificationDomainException emailRequired() {
        return new NotificationDomainException(
                NotificationErrorCode.EMAIL_REQUIRED
        );
    }


    // Generation 관련
    public static NotificationDomainException generationNotPositive(Integer generation) {
        return new NotificationDomainException(
                NotificationErrorCode.INVALID_GENERATION_NOT_POSITIVE,
                generation
        );
    }

    public static NotificationDomainException generationRequired() {
        return new NotificationDomainException(
                NotificationErrorCode.GENERATION_REQUIRED
        );
    }

    // Notification 관련

    public static NotificationDomainException duplicateNotification(String email, Integer generation) {
        return new NotificationDomainException(
                NotificationErrorCode.DUPLICATE_NOTIFICATION,
                email,
                generation
        );
    }


    /**
     * HTTP 상태 코드 반환
     */
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

}
