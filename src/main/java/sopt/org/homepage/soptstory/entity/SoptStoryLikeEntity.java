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
    @Column(name = "\"ip\"", nullable = false, length = 45)
    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"soptStoryId\"")
    private SoptStoryEntity soptStory;

    @Builder
    public SoptStoryLikeEntity(SoptStoryEntity soptStory, String ip) {
        this.soptStory = soptStory;
        this.ip = ip;
    }
}
