package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "\"Logo\"")
public class LogoEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"image\"", nullable = true, length = 500)
    private String image;
    @Basic
    @Column(name = "\"semesterId\"", nullable = false)
    private int semesterId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogoEntity that = (LogoEntity) o;
        return id == that.id && semesterId == that.semesterId && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image, semesterId);
    }
}
