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

}
