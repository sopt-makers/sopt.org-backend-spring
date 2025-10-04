package sopt.org.homepage.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.review.service.query.dto.ReviewsByAuthorView;

import java.util.List;

/**
 * 작성자별 리뷰 목록 응답 DTO
 */
@Schema(description = "작성자별 리뷰 목록 응답")
public record ReviewsByAuthorRes(

        @Schema(description = "리뷰 총 개수")
        int reviewCount,

        @Schema(description = "리뷰 목록")
        List<ReviewRes> reviews
) {
    /**
     * Service 계층의 View를 Controller Response로 변환
     */
    public static ReviewsByAuthorRes from(ReviewsByAuthorView view) {
        List<ReviewRes> reviews = view.reviews().stream()
                .map(ReviewRes::from)
                .toList();

        return new ReviewsByAuthorRes(
                view.reviewCount(),
                reviews
        );
    }
}