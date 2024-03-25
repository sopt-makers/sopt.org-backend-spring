package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "\"Semester\"")
public class SemesterEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"history\"", nullable = true, length = -1)
    private String history;
    @Basic
    @Column(name = "\"color\"", nullable = true, length = 7)
    private String color;
    @Basic
    @Column(name = "\"logo\"", nullable = true, length = -1)
    private String logo;
    @Basic
    @Column(name = "\"background\"", nullable = true, length = -1)
    private String background;
    @Basic
    @Column(name = "\"name\"", nullable = true, length = 30)
    private String name;
    @Basic
    @Column(name = "\"year\"", nullable = false, length = 10)
    private String year;
    @Basic
    @Column(name = "\"coreValue\"", nullable = true, length = 100)
    private String coreValue;
    @Basic
    @Column(name = "\"coreImage\"", nullable = true, length = -1)
    private String coreImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCoreValue() {
        return coreValue;
    }

    public void setCoreValue(String coreValue) {
        this.coreValue = coreValue;
    }

    public String getCoreImage() {
        return coreImage;
    }

    public void setCoreImage(String coreImage) {
        this.coreImage = coreImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemesterEntity that = (SemesterEntity) o;
        return id == that.id && Objects.equals(history, that.history) && Objects.equals(color, that.color) && Objects.equals(logo, that.logo) && Objects.equals(background, that.background) && Objects.equals(name, that.name) && Objects.equals(year, that.year) && Objects.equals(coreValue, that.coreValue) && Objects.equals(coreImage, that.coreImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, history, color, logo, background, name, year, coreValue, coreImage);
    }
}
