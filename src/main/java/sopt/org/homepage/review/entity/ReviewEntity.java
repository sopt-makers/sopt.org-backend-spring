package sopt.org.homepage.review.entity;

import jakarta.persistence.*;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.common.type.Part;

@Entity
@Getter
@Table(name = "\"Review\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private Long id;
    @Basic
    @Column(name = "\"title\"", nullable = false, length = 200)
    private String title;
    @Basic
    @Column(name = "\"author\"", nullable = false, length = 20)
    private String author;
    @Basic
    @Column(name = "\"generation\"", nullable = false)
    private int generation;
    @Enumerated(EnumType.STRING)
    @Column(name = "\"part\"", nullable = false, length = 10)
    private Part part;
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



    @Builder
    public ReviewEntity(String title, String author, Integer generation, Part part,
                        String subject, String thumbnailUrl, String platform,
                        String url, String description, String authorProfileImageUrl) {
        this.title = title;
        this.author = author;
        this.generation = generation;
        this.part = part;
        this.subject = subject;
        this.thumbnailUrl = thumbnailUrl;
        this.platform = platform;
        this.url = url;
        this.description = description;
        this.authorProfileImageUrl = authorProfileImageUrl;
    }




}
