package sopt.org.homepage.sopticle.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
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
    private int authorId;
    @Basic
    @Column(name = "\"authorName\"", nullable = false, length = 20)
    private String authorName;
    @Basic
    @Column(name = "\"authorProfileImageUrl\"", nullable = true, length = 500)
    private String authorProfileImageUrl;
    @Basic
    @Column(name = "\"sopticleUrl\"", nullable = false, length = 500)
    private String sopticleUrl;

    @CreatedDate
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Basic
    @Column(name = "\"likeCount\"", nullable = false)
    private int likeCount;
    @Basic
    @Column(name = "\"pgSopticleId\"", nullable = false)
    private int pgSopticleId;

    @OneToMany(mappedBy = "sopticle", cascade = CascadeType.ALL)
    private List<SopticleLikeEntity> sopticleLikes;

    public Part getPart() {
        return this.part;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }


}
