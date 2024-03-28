package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "\"Review\"")
public class ReviewEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"title\"", nullable = false, length = 200)
    private String title;
    @Basic
    @Column(name = "\"author\"", nullable = false, length = 20)
    private String author;
    @Basic
    @Column(name = "\"generation\"", nullable = false)
    private int generation;
    @Basic
    @Column(name = "\"part\"", nullable = false, length = 10)
    private String part;
    @Basic
    @Column(name = "\"subject\"", nullable = false, length = 20)
    private String subject;
    @Basic
    @Column(name = "\"thumbnailUrl\"", nullable = true, length = 500)
    private String thumbnailUrl;
    @Basic
    @Column(name = "\"platform\"", nullable = false, length = 50)
    private String platform;
    @Basic
    @Column(name = "\"url\"", nullable = false, length = 500)
    private String url;
    @Basic
    @Column(name = "\"description\"", nullable = false, length = 600)
    private String description;
    @Basic
    @Column(name = "\"authorProfileImageUrl\"", nullable = true, length = 500)
    private String authorProfileImageUrl;

}
