package sopt.org.homepage.notification.service.command.dto;

import sopt.org.homepage.notification.domain.Notification;

import java.time.LocalDateTime;

/**
 * Notification 등록 결과
 * - Record로 간결하게 표현
 */
public record NotificationResult(
        Long id,
        String email,
        Integer generation,
        LocalDateTime createdAt
) {
    /**
     * Domain Entity를 Result로 변환
     */
    public static NotificationResult from(Notification notification) {
        return new NotificationResult(
                notification.getId(),
                notification.getEmail().getValue(),
                notification.getGeneration().getValue(),
                notification.getCreatedAt()
        );
    }
}