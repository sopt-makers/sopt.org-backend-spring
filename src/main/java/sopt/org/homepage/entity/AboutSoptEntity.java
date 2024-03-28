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

}
