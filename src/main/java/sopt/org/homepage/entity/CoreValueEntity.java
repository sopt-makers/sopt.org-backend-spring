package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "\"CoreValue\"")
public class CoreValueEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"title\"", nullable = false, length = 100)
    private String title;
    @Basic
    @Column(name = "\"subTitle\"", nullable = false, length = 100)
    private String subTitle;
    @Basic
    @Column(name = "\"imageUrl\"", nullable = false, length = 400)
    private String imageUrl;
    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "\"updatedAt\"", nullable = false)
    private Timestamp updatedAt;
    @Basic
    @Column(name = "\"aboutSoptId\"", nullable = true)
    private Integer aboutSoptId;

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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getAboutSoptId() {
        return aboutSoptId;
    }

    public void setAboutSoptId(Integer aboutSoptId) {
        this.aboutSoptId = aboutSoptId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreValueEntity that = (CoreValueEntity) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(subTitle, that.subTitle) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(aboutSoptId, that.aboutSoptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, subTitle, imageUrl, createdAt, updatedAt, aboutSoptId);
    }
}
