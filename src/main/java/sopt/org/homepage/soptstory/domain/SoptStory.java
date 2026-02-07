package sopt.org.homepage.soptstory.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * SoptStory 엔티티
 * <p>
 * 비즈니스 규칙: - 좋아요 증가/감소 (음수 불가) - URL 중복 방지 (Service에서 검증)
 */
@Entity
@Table(name = "\"SoptStory\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"title\"", nullable = false, length = 100)
    private String title;

    @Column(name = "\"description\"", nullable = false, length = 600)
    private String description;

    @Column(name = "\"thumbnailUrl\"", length = 500)
    private String thumbnailUrl;

    @Column(name = "\"soptStoryUrl\"", nullable = false, length = 500)
    private String url;

    @Column(name = "\"likeCount\"", nullable = false)
    private int likeCount;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "soptStory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoptStoryLike> likes = new ArrayList<>();

    public static SoptStory of(String title, String description, String thumbnailUrl, String url) {
        SoptStory soptStory = new SoptStory();
        soptStory.title = title;
        soptStory.description = description;
        soptStory.thumbnailUrl = thumbnailUrl;
        soptStory.url = url;
        soptStory.likeCount = 0;
        return soptStory;
    }

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount <= 0) {
            throw new IllegalStateException("좋아요 개수는 음수가 될 수 없습니다.");
        }
        this.likeCount--;
    }
}
