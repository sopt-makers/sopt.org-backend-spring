package sopt.org.homepage.activityschedule;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityScheduleRepository extends JpaRepository<ActivitySchedule, Long> {

    List<ActivitySchedule> findByGenerationIdOrderByStartDateAsc(Integer generationId);

    void deleteByGenerationId(Integer generationId);
}
