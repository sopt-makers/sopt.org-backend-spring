package sopt.org.homepage.application.homepage.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"HomepageReview\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomepageReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    public static HomepageReview create(String title, String content, String authorInfo) {
        HomepageReview review = new HomepageReview();
        review.title = title;
        review.content = content;
        review.authorInfo = authorInfo;
        return review;
    }

    @Column(name = "\"title\"", nullable = false)
    private String title;

    @Column(name = "\"content\"", nullable = false, length = 200)
    private String content;

    @Column(name = "\"authorInfo\"", nullable = false)
    private String authorInfo;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    public void update(String title, String content, String authorInfo) {
        this.title = title;
        this.content = content;
        this.authorInfo = authorInfo;
    }
}
