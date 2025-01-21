package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "\"Partner\"")
public class PartnerEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"name\"", nullable = true, length = 50)
    private String name;
    @Basic
    @Column(name = "\"image\"", nullable = true, length = 500)
    private String image;

}
