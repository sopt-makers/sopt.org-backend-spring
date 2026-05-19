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

    @Column(name = "\"startDate\"", nullable = false)
    private LocalDate startDate;

    @Column(name = "\"endDate\"")
    private LocalDate endDate;

    @Column(name = "\"displayOrder\"", nullable = false)
    private Integer displayOrder;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    public static ActivitySchedule create(Integer generationId, String name, LocalDate startDate, LocalDate endDate, Integer displayOrder) {
        ActivitySchedule schedule = new ActivitySchedule();
        schedule.generationId = generationId;
        schedule.name = name;
        schedule.startDate = startDate;
        schedule.endDate = endDate;
        schedule.displayOrder = displayOrder;
        return schedule;
    }
}
