package sopt.org.homepage.notification.dto;

import java.time.LocalDateTime;
import sopt.org.homepage.notification.Notification;

public record RegisterNotificationResponse(
        Long id,
        String email,
        Integer generation,
        LocalDateTime createdAt
) {
    public static RegisterNotificationResponse from(Notification notification) {
        return new RegisterNotificationResponse(
                notification.getId(),
                notification.getEmail(),           // ✅ 직접 접근 (VO 없음)
                notification.getGeneration(),      // ✅ 직접 접근 (VO 없음)
                notification.getCreatedAt()
        );
    }
}
