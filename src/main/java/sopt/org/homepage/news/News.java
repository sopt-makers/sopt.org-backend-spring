package sopt.org.homepage.news;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * News 엔티티 (최신소식)
 * <p>
 * 테이블: MainNews
 */
@Entity
@Table(name = "main_news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "instagram_post_id", unique = true)
    private String instagramPostId;

    @Column(name = "published_at")
    private Instant publishedAt;

    /** 수집 게시물 식별자와 원본 업로드 시각을 보존해 뉴스를 생성한다. */
    public static News fromInstagramPost(String title, String link, String image, String postId, Instant publishedAt) {
        News news = new News(image, title, link);
        news.instagramPostId = postId;
        news.publishedAt = publishedAt;
        return news;
    }

    /** 수동 뉴스도 등록 시각으로 정렬하며, 인스타 원본 게시일은 그대로 보존한다. */
    @PrePersist
    private void initializePublishedAt() {
        if (publishedAt == null) publishedAt = Instant.now();
    }

    /** 원본 게시 시각을 홈페이지 표시용 한국 날짜로 변환한다. */
    public String getPublishedDate() {
        return publishedAt == null ? null : publishedAt.atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public News(String image, String title, String link) {
        this.image = image;
        this.title = title;
        this.link = link;
    }

    public void update(String image, String title, String link) {
        if (image != null) this.image = image;
        this.title = title;
        this.link = link;
    }

}
