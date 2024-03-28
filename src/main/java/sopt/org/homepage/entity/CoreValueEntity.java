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

}
