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

}
