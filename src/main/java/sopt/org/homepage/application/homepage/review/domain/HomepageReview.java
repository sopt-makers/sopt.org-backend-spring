package sopt.org.homepage.application.homepage.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "homepage_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomepageReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * @param type null이면 활동 후기(ACTIVITY)로 생성
     */
    public static HomepageReview create(HomepageReviewType type, String title, String content, String authorInfo) {
        HomepageReview review = new HomepageReview();
        review.type = HomepageReviewType.orDefault(type);
        review.title = title;
        review.content = content;
        review.authorInfo = authorInfo;
        return review;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private HomepageReviewType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @Column(name = "author_info", nullable = false)
    private String authorInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * @param type null이면 기존 종류 유지
     */
    public void update(HomepageReviewType type, String title, String content, String authorInfo) {
        if (type != null) {
            this.type = type;
        }
        this.title = title;
        this.content = content;
        this.authorInfo = authorInfo;
    }
}
