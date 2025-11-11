package sopt.org.homepage.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import sopt.org.homepage.notification.domain.Notification;

@Schema(description = "모집 알림 신청 응답")
public record RegisterNotificationResponse(

        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "이메일", example = "example@sopt.org")
        String email,

        @Schema(description = "기수", example = "35")
        Integer generation,

        @Schema(description = "등록 일시")
        LocalDateTime createdAt
) {
    public static RegisterNotificationResponse from(Notification notification) {
        return new RegisterNotificationResponse(
                notification.getId(),
                notification.getEmail().getValue(),
                notification.getGeneration().getValue(),
                notification.getCreatedAt()

        );
    }
}
