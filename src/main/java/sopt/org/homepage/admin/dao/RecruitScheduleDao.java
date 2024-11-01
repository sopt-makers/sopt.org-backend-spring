package sopt.org.homepage.admin.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitScheduleDao {
    private String type;           // "OB" or "YB"
    private Schedule schedule;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Schedule {
    private String applicationStartTime;
    private String applicationEndTime;
    private String applicationResultTime;
    private String interviewStartTime;
    private String interviewEndTime;
    private String finalResultTime;
}