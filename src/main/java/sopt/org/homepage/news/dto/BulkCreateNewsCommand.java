package sopt.org.homepage.news.dto;

import sopt.org.homepage.news.NewsType;

import java.util.List;
import lombok.Builder;

@Builder
public record BulkCreateNewsCommand(List<NewsData> news) {

    @Builder
    public record NewsData(String title, String link, String imageUrl, NewsType type) {
        public NewsData(String title, String link, String imageUrl) {
            this(title, link, imageUrl, null);
        }
    }
}
