package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

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
    private Timestamp createdAt;
    @Basic
    @Column(name = "\"sopticleId\"", nullable = true)
    private Integer sopticleId;

}
