package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "\"Sopticle\"")
public class SopticleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"part\"", nullable = false, length = 20)
    private String part;
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
    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "\"likeCount\"", nullable = false)
    private int likeCount;
    @Basic
    @Column(name = "\"pgSopticleId\"", nullable = false)
    private int pgSopticleId;

}
