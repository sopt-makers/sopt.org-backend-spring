package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumWeekDao {
    private Integer week;          // 1-8주차
    private String description;    // 커리큘럼 설명
}
