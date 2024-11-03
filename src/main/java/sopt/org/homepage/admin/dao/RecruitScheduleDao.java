package sopt.org.homepage.admin.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitScheduleDao {
    private String type;           // "OB" or "YB"
    private ScheduleDao schedule;
}

