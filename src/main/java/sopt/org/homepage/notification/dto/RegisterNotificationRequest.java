package sopt.org.homepage.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "모집 알림 신청 요청")
public record RegisterNotificationRequest(

        @Schema(description = "이메일", example = "example@sopt.org", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @Schema(description = "기수", example = "35", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "기수는 필수입니다")
        @Positive(message = "기수는 양수여야 합니다")
        Integer generation
) {
}
