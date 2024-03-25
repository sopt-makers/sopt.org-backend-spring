package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "\"CooperationProject\"")
public class CooperationProjectEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"year\"", nullable = true)
    private Integer year;
    @Basic
    @Column(name = "\"title\"", nullable = true, length = 50)
    private String title;
    @Basic
    @Column(name = "\"content\"", nullable = true, length = 300)
    private String content;
    @Basic
    @Column(name = "\"subContent\"", nullable = true, length = 300)
    private String subContent;
    @Basic
    @Column(name = "\"posterImage\"", nullable = true, length = 500)
    private String posterImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CooperationProjectEntity that = (CooperationProjectEntity) o;
        return id == that.id && Objects.equals(year, that.year) && Objects.equals(title, that.title) && Objects.equals(content, that.content) && Objects.equals(subContent, that.subContent) && Objects.equals(posterImage, that.posterImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, title, content, subContent, posterImage);
    }
}
