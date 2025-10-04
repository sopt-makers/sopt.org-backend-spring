package sopt.org.homepage.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.review.service.query.dto.ReviewSummaryView;

import java.util.List;

/**
 * 리뷰 응답 DTO
 */
@Schema(description = "리뷰 응답")
public record ReviewRes(

        @Schema(description = "리뷰 ID")
        Long id,

        @Schema(description = "제목")
        String title,

        @Schema(description = "작성자")
        String author,

        @Schema(description = "작성자 프로필 이미지 URL")
        String authorProfileImageUrl,

        @Schema(description = "기수")
        Integer generation,

        @Schema(description = "설명")
        String description,

        @Schema(description = "파트")
        Part part,

        @Schema(description = "카테고리")
        String category,

        @Schema(description = "세부 주제")
        List<String> subject,

        @Schema(description = "썸네일 URL")
        String thumbnailUrl,

        @Schema(description = "플랫폼")
        String platform,

        @Schema(description = "리뷰 URL")
        String url
) {
    /**
     * Service 계층의 View를 Controller Response로 변환
     */
    public static ReviewRes from(ReviewSummaryView view) {
        return new ReviewRes(
                view.id(),
                view.title(),
                view.author(),
                view.authorProfileImageUrl(),
                view.generation(),
                view.description(),
                view.part(),
                view.category(),
                view.subject(),
                view.thumbnailUrl(),
                view.platform(),
                view.url()
        );
    }
}