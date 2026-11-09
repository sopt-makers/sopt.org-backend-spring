package sopt.org.homepage.application.homepage.review.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.application.homepage.review.domain.HomepageReviewType;

@Builder
public record BulkCreateHomepageReviewsCommand(List<ReviewData> reviews) {

    @Builder
    public record ReviewData(HomepageReviewType type, String title, String content, String authorInfo) {
    }
}
