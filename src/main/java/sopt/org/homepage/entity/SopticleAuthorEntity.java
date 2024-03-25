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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPgUserId() {
        return pgUserId;
    }

    public void setPgUserId(int pgUserId) {
        this.pgUserId = pgUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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

    public Integer getSopticleId() {
        return sopticleId;
    }

    public void setSopticleId(Integer sopticleId) {
        this.sopticleId = sopticleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SopticleAuthorEntity that = (SopticleAuthorEntity) o;
        return id == that.id && pgUserId == that.pgUserId && generation == that.generation && Objects.equals(name, that.name) && Objects.equals(profileImage, that.profileImage) && Objects.equals(part, that.part) && Objects.equals(sopticleId, that.sopticleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pgUserId, name, profileImage, generation, part, sopticleId);
    }
}
