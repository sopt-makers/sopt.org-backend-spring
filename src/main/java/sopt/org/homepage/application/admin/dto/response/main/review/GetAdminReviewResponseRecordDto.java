package sopt.org.homepage.application.admin.dto.response.main.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import sopt.org.homepage.application.homepage.review.domain.HomepageReview;

@Schema(description = "리뷰")
@Builder
public record GetAdminReviewResponseRecordDto(
        @Schema(description = "리뷰 ID", requiredMode = Schema.RequiredMode.REQUIRED) Long id,
        @Schema(description = "리뷰 제목", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(description = "리뷰 내용", requiredMode = Schema.RequiredMode.REQUIRED) String content,
        @Schema(description = "작성자 정보", requiredMode = Schema.RequiredMode.REQUIRED) String authorInfo
) {
    public static GetAdminReviewResponseRecordDto from(HomepageReview review) {
        return new GetAdminReviewResponseRecordDto(
                review.getId(),
                review.getTitle(),
                review.getContent(),
                review.getAuthorInfo()
        );
    }
}
