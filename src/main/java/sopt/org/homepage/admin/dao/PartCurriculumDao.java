package sopt.org.homepage.admin.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PartCurriculumDao {
    private String part;
    private List<CurriculumWeek> weeks;
}


@Getter
@Setter
@NoArgsConstructor
class CurriculumWeek {
    private Integer week;          // 1-8주차
    private String description;    // 커리큘럼 설명
}
