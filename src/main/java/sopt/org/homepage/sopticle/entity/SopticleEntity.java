package sopt.org.homepage.sopticle.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sopt.org.homepage.common.type.Part;

@Entity
@Getter
@Table(name = "\"Sopticle\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SopticleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "\"part\"")
    private Part part;

    @Basic
    @Column(name = "\"generation\"", nullable = false)
    private int generation;

    @Basic
    @Column(name = "\"thumbnailUrl\"", nullable = true, length = 500)
    private String thumbnailUrl;

    @Basic
    @Column(name = "\"title\"", nullable = false, length = 100)
    private String title;

    @Basic
    @Column(name = "\"description\"", nullable = false, length = 600)
    private String description;

    @Basic
    @Column(name = "\"authorId\"", nullable = false)
    private Long authorId;

    @Basic
    @Column(name = "\"authorName\"", nullable = false, length = 20)
    private String authorName;

    @Basic
    @Column(name = "\"authorProfileImageUrl\"", nullable = true, length = 500)
    private String authorProfileImageUrl;

    @Basic
    @Column(name = "\"sopticleUrl\"", nullable = false, length = 500)
    private String sopticleUrl;

    @CreationTimestamp
    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt;

    @Basic
    @Column(name = "\"likeCount\"", nullable = false)
    private int likeCount;

    @Basic
    @Column(name = "\"pgSopticleId\"", nullable = false)
    private Long pgSopticleId;

    @OneToMany(mappedBy = "sopticle", cascade = CascadeType.ALL)
    private List<SopticleLikeEntity> sopticleLikes;

    @OneToMany(mappedBy = "sopticle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SopticleAuthorEntity> authors = new ArrayList<>();


    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }

    @Builder
    private SopticleEntity(Part part, Integer generation, String thumbnailUrl, String title,
                           String description, Long authorId, String authorName,
                           String authorProfileImageUrl, String sopticleUrl, Long pgSopticleId) {
        this.part = part;
        this.generation = generation;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorProfileImageUrl = authorProfileImageUrl;
        this.sopticleUrl = sopticleUrl;
        this.pgSopticleId = pgSopticleId;
    }


}
