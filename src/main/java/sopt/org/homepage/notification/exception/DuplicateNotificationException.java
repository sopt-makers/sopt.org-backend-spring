package sopt.org.homepage.notification.exception;

import sopt.org.homepage.exception.ClientBadRequestException;

/**
 * Notification 도메인 예외
 */
public class DuplicateNotificationException extends ClientBadRequestException {

    public DuplicateNotificationException(String email, Integer generation) {
        super(String.format(
                "이미 등록된 알림입니다. (이메일: %s, 기수: %d)",
                email, generation
        ));
    }
}