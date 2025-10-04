package sopt.org.homepage.review.service.query.dto;

import sopt.org.homepage.review.domain.Review;

import java.util.List;

/**
 * 작성자별 리뷰 목록 뷰
 */
public record ReviewsByAuthorView(
        int reviewCount,
        List<ReviewSummaryView> reviews
) {
    /**
     * Review 리스트로부터 뷰 생성
     */
    public static ReviewsByAuthorView from(List<Review> reviews) {
        List<ReviewSummaryView> summaries = reviews.stream()
                .map(ReviewSummaryView::from)
                .toList();

        return new ReviewsByAuthorView(
                reviews.size(),
                summaries
        );
    }
}