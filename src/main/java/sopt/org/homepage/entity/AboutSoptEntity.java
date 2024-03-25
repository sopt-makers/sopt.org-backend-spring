package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "\"AboutSopt\"")
public class AboutSoptEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"isPublished\"", nullable = false)
    private boolean isPublished;
    @Basic
    @Column(name = "\"title\"", nullable = false, length = 200)
    private String title;
    @Basic
    @Column(name = "\"bannerImage\"", nullable = false, length = 400)
    private String bannerImage;
    @Basic
    @Column(name = "\"coreDescription\"", nullable = false, length = 400)
    private String coreDescription;
    @Basic
    @Column(name = "\"planCurriculum\"", nullable = false, length = 400)
    private String planCurriculum;
    @Basic
    @Column(name = "\"designCurriculum\"", nullable = false, length = 400)
    private String designCurriculum;
    @Basic
    @Column(name = "\"androidCurriculum\"", nullable = false, length = 400)
    private String androidCurriculum;
    @Basic
    @Column(name = "\"iosCurriculum\"", nullable = false, length = 400)
    private String iosCurriculum;
    @Basic
    @Column(name = "\"webCurriculum\"", nullable = false, length = 400)
    private String webCurriculum;
    @Basic
    @Column(name = "\"serverCurriculum\"", nullable = false, length = 400)
    private String serverCurriculum;
    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "\"updatedAt\"", nullable = false)
    private Timestamp updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getCoreDescription() {
        return coreDescription;
    }

    public void setCoreDescription(String coreDescription) {
        this.coreDescription = coreDescription;
    }

    public String getPlanCurriculum() {
        return planCurriculum;
    }

    public void setPlanCurriculum(String planCurriculum) {
        this.planCurriculum = planCurriculum;
    }

    public String getDesignCurriculum() {
        return designCurriculum;
    }

    public void setDesignCurriculum(String designCurriculum) {
        this.designCurriculum = designCurriculum;
    }

    public String getAndroidCurriculum() {
        return androidCurriculum;
    }

    public void setAndroidCurriculum(String androidCurriculum) {
        this.androidCurriculum = androidCurriculum;
    }

    public String getIosCurriculum() {
        return iosCurriculum;
    }

    public void setIosCurriculum(String iosCurriculum) {
        this.iosCurriculum = iosCurriculum;
    }

    public String getWebCurriculum() {
        return webCurriculum;
    }

    public void setWebCurriculum(String webCurriculum) {
        this.webCurriculum = webCurriculum;
    }

    public String getServerCurriculum() {
        return serverCurriculum;
    }

    public void setServerCurriculum(String serverCurriculum) {
        this.serverCurriculum = serverCurriculum;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AboutSoptEntity that = (AboutSoptEntity) o;
        return id == that.id && isPublished == that.isPublished && Objects.equals(title, that.title) && Objects.equals(bannerImage, that.bannerImage) && Objects.equals(coreDescription, that.coreDescription) && Objects.equals(planCurriculum, that.planCurriculum) && Objects.equals(designCurriculum, that.designCurriculum) && Objects.equals(androidCurriculum, that.androidCurriculum) && Objects.equals(iosCurriculum, that.iosCurriculum) && Objects.equals(webCurriculum, that.webCurriculum) && Objects.equals(serverCurriculum, that.serverCurriculum) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isPublished, title, bannerImage, coreDescription, planCurriculum, designCurriculum, androidCurriculum, iosCurriculum, webCurriculum, serverCurriculum, createdAt, updatedAt);
    }
}
