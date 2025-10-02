package sopt.org.homepage.notification.exception;

import sopt.org.homepage.exception.ClientBadRequestException;

/**
 * 유효하지 않은 Notification 데이터
 */
public class InvalidNotificationException extends ClientBadRequestException {

    public InvalidNotificationException(String message) {
        super("유효하지 않은 알림 신청: " + message);
    }
}