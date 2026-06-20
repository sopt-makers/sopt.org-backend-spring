package sopt.org.homepage.application.admin.dto.response.main.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "홈페이지 리뷰 추가")
@Builder
public record AddAdminReviewResponseRecordDto(
        @Schema(description = "성공 메시지", requiredMode = Schema.RequiredMode.REQUIRED) String message
) {
    public static AddAdminReviewResponseRecordDto success() {
        return new AddAdminReviewResponseRecordDto("홈페이지 리뷰 추가 성공");
    }
}
