package sopt.org.homepage.application.admin.dto.response.main.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "홈페이지 리뷰 수정")
@Builder
public record EditAdminReviewResponseRecordDto(
        @Schema(description = "성공 메시지", requiredMode = Schema.RequiredMode.REQUIRED) String message
) {
    public static EditAdminReviewResponseRecordDto success() {
        return new EditAdminReviewResponseRecordDto("홈페이지 리뷰 수정 성공");
    }
}
