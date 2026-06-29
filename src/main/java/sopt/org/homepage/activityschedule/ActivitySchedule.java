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
@Table(name = "activity_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "generation_id", nullable = false)
    private Integer generationId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ActivitySchedule create(Integer generationId, String name, LocalDate startDate, LocalDate endDate) {
        ActivitySchedule schedule = new ActivitySchedule();
        schedule.generationId = generationId;
        schedule.name = name;
        schedule.startDate = startDate;
        schedule.endDate = endDate;
        return schedule;
    }
}
