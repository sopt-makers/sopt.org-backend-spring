package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "\"SopticleLike\"")
public class SopticleLikeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"sessionId\"", nullable = false, length = 50)
    private String sessionId;
    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "\"sopticleId\"", nullable = true)
    private Integer sopticleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
        SopticleLikeEntity that = (SopticleLikeEntity) o;
        return id == that.id && Objects.equals(sessionId, that.sessionId) && Objects.equals(createdAt, that.createdAt) && Objects.equals(sopticleId, that.sopticleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionId, createdAt, sopticleId);
    }
}
