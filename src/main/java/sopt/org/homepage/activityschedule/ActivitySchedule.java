package sopt.org.homepage.activityschedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "\"ActivitySchedule\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Column(name = "\"generationId\"", nullable = false)
    private Integer generationId;

    @Column(name = "\"name\"", nullable = false, length = 100)
    private String name;

    @Column(name = "\"date\"", nullable = false)
    private LocalDate date;

    @Column(name = "\"displayOrder\"", nullable = false)
    private Integer displayOrder;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    public static ActivitySchedule create(Integer generationId, String name, LocalDate date, Integer displayOrder) {
        ActivitySchedule schedule = new ActivitySchedule();
        schedule.generationId = generationId;
        schedule.name = name;
        schedule.date = date;
        schedule.displayOrder = displayOrder;
        return schedule;
    }
}
