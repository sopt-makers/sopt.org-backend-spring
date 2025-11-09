package sopt.org.homepage.review.service.query.dto;

import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.review.domain.Review;

import java.util.List;

/**
 * 리뷰 요약 뷰 (목록 조회용)
 */
public record ReviewSummaryView(
        Long id,
        String title,
        String author,
        String authorProfileImageUrl,
        Integer generation,
        String description,
        PartType partType,
        String category,
        List<String> subject,
        String thumbnailUrl,
        String platform,
        String url
) {
    /**
     * Review 엔티티로부터 뷰 생성
     */
    public static ReviewSummaryView from(Review review) {
        return new ReviewSummaryView(
                review.getId(),
                review.getTitle(),
                review.getAuthorName(),
                review.getAuthorProfileImageUrl(),
                review.getGeneration(),
                review.getDescription(),
                review.getPartType(),
                review.getCategoryValue(),
                review.getSubjectValues(),
                review.getThumbnailUrl(),
                review.getPlatform(),
                review.getUrlValue()
        );
    }
}