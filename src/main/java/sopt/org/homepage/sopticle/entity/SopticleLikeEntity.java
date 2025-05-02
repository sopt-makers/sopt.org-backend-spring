package sopt.org.homepage.sopticle.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import lombok.Getter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Entity
@Table(name = "\"SopticleLike\"")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SopticleLikeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Basic
    @Column(name = "\"sessionId\"", nullable = false, length = 50)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"sopticleId\"")
    private SopticleEntity sopticle;

    @Builder
    public SopticleLikeEntity(SopticleEntity sopticle, String sessionId) {
        this.sopticle = sopticle;
        this.sessionId = sessionId;
    }
}
