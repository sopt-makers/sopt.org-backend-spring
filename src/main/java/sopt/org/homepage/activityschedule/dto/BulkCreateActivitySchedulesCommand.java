package sopt.org.homepage.activityschedule.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record BulkCreateActivitySchedulesCommand(Integer generationId, List<ActivityScheduleData> activitySchedules) {

    @Builder
    public record ActivityScheduleData(String name, LocalDate startDate, LocalDate endDate, Integer displayOrder) {
    }
}
