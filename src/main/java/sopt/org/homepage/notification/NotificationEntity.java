package sopt.org.homepage.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@Setter
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
