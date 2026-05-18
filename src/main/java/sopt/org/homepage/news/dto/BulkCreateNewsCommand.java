package sopt.org.homepage.news.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record BulkCreateNewsCommand(List<NewsData> news) {

    @Builder
    public record NewsData(String title, String link, String imageUrl) {
    }
}
