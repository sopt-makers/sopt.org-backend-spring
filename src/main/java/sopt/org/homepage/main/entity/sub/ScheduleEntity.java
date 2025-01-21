package sopt.org.homepage.main.entity.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntity {
    private String applicationStartTime;
    private String applicationEndTime;
    private String applicationResultTime;
    private String interviewStartTime;
    private String interviewEndTime;
    private String finalResultTime;
}
