package sopt.org.homepage.sopticle.entity;

import jakarta.persistence.*;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "\"SopticleAuthor\"")
public class SopticleAuthorEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;

    @Basic
    @Column(name = "\"pgUserId\"", nullable = false)
    private Long pgUserId;

    @Basic
    @Column(name = "\"name\"", nullable = false, length = 20)
    private String name;

    @Basic
    @Column(name = "\"profileImage\"", nullable = true, length = 500)
    private String profileImage;

    @Basic
    @Column(name = "\"generation\"", nullable = false)
    private int generation;

    @Basic
    @Column(name = "\"part\"", nullable = false, length = 20)
    private String part;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"sopticleId\"")
    private SopticleEntity sopticle;

    @Builder
    private SopticleAuthorEntity(SopticleEntity sopticle, Long pgUserId, String name,
                                 String profileImage, Integer generation, String part) {
        this.sopticle = sopticle;
        this.pgUserId = pgUserId;
        this.name = name;
        this.profileImage = profileImage;
        this.generation = generation;
        this.part = part;
    }

}
