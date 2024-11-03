package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDao {
    private String applicationStartTime;
    private String applicationEndTime;
    private String applicationResultTime;
    private String interviewStartTime;
    private String interviewEndTime;
    private String finalResultTime;
}
