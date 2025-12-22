package sopt.org.homepage.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 중복 알림 등록 시 발생하는 예외
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateNotificationException extends RuntimeException {

    public DuplicateNotificationException(String email, Integer generation) {
        super(String.format(
                "이미 등록된 알림입니다. (이메일: %s, 기수: %d)",
                email, generation
        ));
    }
}
