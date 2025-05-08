package sopt.org.homepage.soptstory.entity;

import jakarta.persistence.*;


import lombok.Getter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Entity
@Table(name = "\"SoptStoryLike\"")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptStoryLikeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Basic
    @Column(name = "\"sessionId\"", nullable = false, length = 50)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"soptStoryId\"")
    private SoptStoryEntity soptStory;

    @Builder
    public SoptStoryLikeEntity(SoptStoryEntity soptStory, String sessionId) {
        this.soptStory = soptStory;
        this.sessionId = sessionId;
    }
}
