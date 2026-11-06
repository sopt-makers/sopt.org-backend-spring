package sopt.org.homepage.news.instagram;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsRepository;

@Service
@RequiredArgsConstructor
public class InstagramNewsPostProcessor {
    private final InstagramNewsConfig.Properties properties;

    private final NewsRepository newsRepository;
    private final InstagramNewsFilter instagramNewsFilter;
    private final InstagramThumbnailStorage instagramThumbnailStorage;

    /** 계정·기간·중복·캡션을 검증하고 외부 I/O 후 repository 트랜잭션으로 저장한다. */
    public void process(InstagramPost post) {
        if (!shouldProcess(post)) return;

        String thumbnail = post.thumbnail();
        if (thumbnail == null || thumbnail.isBlank()) {
            throw new IllegalStateException("Thumbnail missing");
        }

        String image = instagramThumbnailStorage.store(post.id(), thumbnail);
        News news = News.fromInstagramPost(
                instagramNewsFilter.title(post.caption()), post.url(), image, post.id(), post.timestamp());
        newsRepository.save(news);
    }

    private boolean shouldProcess(InstagramPost post) {
        return isValidPost(post)
                && !newsRepository.existsByInstagramPostId(post.id())
                && instagramNewsFilter.shouldPublish(post.caption());
    }

    private boolean isValidPost(InstagramPost post) {
        return post != null
                && post.id() != null && post.id().matches("[0-9]+")
                && ApifyInstagramClient.ACCOUNT.equals(post.ownerUsername())
                && post.timestamp() != null
                && !post.timestamp().isBefore(properties.since())
                && !post.timestamp().isAfter(Instant.now())
                && post.url() != null
                && post.url().matches("https://www[.]instagram[.]com/(p|reel|tv)/[A-Za-z0-9_-]+/?");
    }
}
