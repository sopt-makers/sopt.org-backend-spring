package sopt.org.homepage.activityschedule;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityScheduleRepository extends JpaRepository<ActivitySchedule, Long> {

    List<ActivitySchedule> findByGenerationIdOrderByStartDateAsc(Integer generationId);

    @Modifying
    @Query("DELETE FROM ActivitySchedule a WHERE a.generationId = :generationId")
    void deleteByGenerationId(@Param("generationId") Integer generationId);
}
