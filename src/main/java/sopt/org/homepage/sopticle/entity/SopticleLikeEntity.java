package sopt.org.homepage.sopticle.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "\"SopticleLike\"")
public class SopticleLikeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;

    @Basic
    @Column(name = "\"sessionId\"", nullable = false, length = 50)
    private String sessionId;

    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"sopticleId\"")
    private SopticleEntity sopticle;

}
