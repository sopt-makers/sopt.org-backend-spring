package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "\"SopticleAuthor\"")
public class SopticleAuthorEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"pgUserId\"", nullable = false)
    private int pgUserId;
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
    @Basic
    @Column(name = "\"sopticleId\"", nullable = true)
    private Integer sopticleId;

}
