package sopt.org.homepage.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "migration_history")
public class MigrationHistoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "timestamp", nullable = false)
    private long timestamp;
    @Basic
    @Column(name = "name", nullable = false, length = -1)
    private String name;

}
