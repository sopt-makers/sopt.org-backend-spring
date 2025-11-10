package sopt.org.homepage.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import sopt.org.homepage.common.type.PartType;

import java.util.List;

/**
 * 리뷰 생성 요청 DTO
 */
@Schema(description = "리뷰 생성 요청")
public record CreateReviewReq(
        @Schema(description = "기수", example = "34")
        @NotNull(message = "기수는 필수입니다.")
        @Positive(message = "기수는 1 이상이어야 합니다.")
        Integer generation,

        @Schema(description = "파트", example = "SERVER")
        @NotNull(message = "파트는 필수입니다.")
        PartType partType,

        @Schema(description = "메인 카테고리", example = "전체 활동")
        @NotBlank(message = "카테고리는 필수입니다.")
        String mainCategory,

        @Schema(description = "세부 활동 목록 (전체 활동일 때만)", example = "[\"세미나\", \"프로젝트\"]")
        List<String> subActivities,

        @Schema(description = "세부 리크루팅 (서류/면접일 때만)", example = "서류")
        String subRecruiting,

        @Schema(description = "작성자명", example = "홍길동")
        @NotBlank(message = "작성자명은 필수입니다.")
        String author,

        @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String authorProfileImageUrl,

        @Schema(description = "리뷰 URL (링크)", example = "https://medium.com/@sopt/review-article")
        @NotBlank(message = "리뷰 URL은 필수입니다.")
        String link

) {
}