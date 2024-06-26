package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "\"Notification\"")
public class NotificationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "\"id\"", nullable = false)
    private int id;
    @Basic
    @Column(name = "\"generation\"", nullable = false)
    private int generation;
    @Basic
    @Column(name = "\"email\"", nullable = false, length = 255)
    private String email;
    @Basic
    @Column(name = "\"createdAt\"", nullable = false)
    private Timestamp createdAt;

}
