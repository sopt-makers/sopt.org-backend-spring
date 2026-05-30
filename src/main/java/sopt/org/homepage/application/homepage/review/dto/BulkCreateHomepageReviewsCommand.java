package sopt.org.homepage.application.homepage.review.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record BulkCreateHomepageReviewsCommand(List<ReviewData> reviews) {

    @Builder
    public record ReviewData(String title, String content, String authorInfo) {
    }
}
