package sopt.org.homepage.admin.dao;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartCurriculumDao {
    private String part;
    private List<CurriculumWeekDao> weeks;
}


