package sopt.org.homepage.news;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "\"MainNews\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Integer id;

    @Column(name = "\"image\"", nullable = false)
    private String image;

    @Column(name = "\"title\"", nullable = false)
    private String title;

    @Column(name = "\"link\"", nullable = false)
    private String link;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public News(String image, String title, String link) {
        this.image = image;
        this.title = title;
        this.link = link;
    }

}
