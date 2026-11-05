package sopt.org.homepage.news.instagram;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsRepository;

@Service
@RequiredArgsConstructor
public class InstagramNewsPostProcessor {
    private static final Instant INSTAGRAM_POSTS_SINCE = Instant.parse("2025-12-31T15:00:00Z");

    private final NewsRepository newsRepository;
    private final InstagramNewsFilter instagramNewsFilter;
    private final InstagramThumbnailStorage instagramThumbnailStorage;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(InstagramPost post) {
        if (post.id() == null || !post.id().matches("[0-9]+")
                || !ApifyInstagramClient.ACCOUNT.equals(post.ownerUsername())
                || post.timestamp() == null || post.timestamp().isBefore(INSTAGRAM_POSTS_SINCE)
                || post.timestamp().isAfter(Instant.now()) || newsRepository.existsByInstagramPostId(post.id())) return;
        if (post.url() == null || !post.url().matches("https://www[.]instagram[.]com/(p|reel|tv)/[A-Za-z0-9_-]+/?")) return;
        if (!instagramNewsFilter.shouldPublish(post.caption())) return;
        if (post.thumbnail() == null || post.thumbnail().isBlank()) throw new IllegalStateException("Thumbnail missing");
        String image = instagramThumbnailStorage.store(post.id(), post.thumbnail());
        newsRepository.save(News.fromInstagramPost(instagramNewsFilter.title(post.caption()), post.url(), image, post.id(), post.timestamp()));
    }

}
