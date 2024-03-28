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

}
