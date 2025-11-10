package sopt.org.homepage.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.notification.service.command.dto.NotificationResult;

import java.time.LocalDateTime;

/**
 * 모집 알림 신청 응답
 */
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
    /**
     * Service Result를 Response로 변환
     */
    public static RegisterNotificationResponse from(NotificationResult result) {
        return new RegisterNotificationResponse(
                result.id(),
                result.email(),
                result.generation(),
                result.createdAt()
        );
    }
}