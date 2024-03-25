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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorProfileImageUrl() {
        return authorProfileImageUrl;
    }

    public void setAuthorProfileImageUrl(String authorProfileImageUrl) {
        this.authorProfileImageUrl = authorProfileImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity that = (ReviewEntity) o;
        return id == that.id && generation == that.generation && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(part, that.part) && Objects.equals(subject, that.subject) && Objects.equals(thumbnailUrl, that.thumbnailUrl) && Objects.equals(platform, that.platform) && Objects.equals(url, that.url) && Objects.equals(description, that.description) && Objects.equals(authorProfileImageUrl, that.authorProfileImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, generation, part, subject, thumbnailUrl, platform, url, description, authorProfileImageUrl);
    }
}
