package sopt.org.homepage.soptstory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SoptStory 좋아요 엔티티
 * <p>
 * 비즈니스 규칙: - IP 기반 중복 좋아요 방지 (DB unique constraint)
 */
@Entity
@Table(
        name = "\"SoptStoryLike\"",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_soptstory_like_ip",
                        columnNames = {"\"soptStoryId\"", "\"ip\""}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptStoryLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"soptStoryId\"", nullable = false)
    private SoptStory soptStory;

    @Column(name = "\"ip\"", nullable = false, length = 45)
    private String ip;

    public static SoptStoryLike of(SoptStory soptStory, String ip) {
        SoptStoryLike like = new SoptStoryLike();
        like.soptStory = soptStory;
        like.ip = ip;
        return like;
    }
}
