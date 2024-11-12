package sopt.org.homepage.main.entity.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitScheduleEntity {
    private String type;           // "OB" or "YB"
    private ScheduleEntity schedule;
}

