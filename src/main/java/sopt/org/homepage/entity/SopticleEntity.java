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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfileImageUrl() {
        return authorProfileImageUrl;
    }

    public void setAuthorProfileImageUrl(String authorProfileImageUrl) {
        this.authorProfileImageUrl = authorProfileImageUrl;
    }

    public String getSopticleUrl() {
        return sopticleUrl;
    }

    public void setSopticleUrl(String sopticleUrl) {
        this.sopticleUrl = sopticleUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPgSopticleId() {
        return pgSopticleId;
    }

    public void setPgSopticleId(int pgSopticleId) {
        this.pgSopticleId = pgSopticleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SopticleEntity that = (SopticleEntity) o;
        return id == that.id && generation == that.generation && authorId == that.authorId && likeCount == that.likeCount && pgSopticleId == that.pgSopticleId && Objects.equals(part, that.part) && Objects.equals(thumbnailUrl, that.thumbnailUrl) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(authorName, that.authorName) && Objects.equals(authorProfileImageUrl, that.authorProfileImageUrl) && Objects.equals(sopticleUrl, that.sopticleUrl) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, part, generation, thumbnailUrl, title, description, authorId, authorName, authorProfileImageUrl, sopticleUrl, createdAt, likeCount, pgSopticleId);
    }
}
