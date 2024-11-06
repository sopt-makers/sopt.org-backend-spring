package sopt.org.homepage.admin.entity.sub;

import lombok.*;

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
