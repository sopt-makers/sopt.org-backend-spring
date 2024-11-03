package sopt.org.homepage.admin.dao;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitPartCurriculumDao {
    private String part;
    private IntroductionDao introduction;
}

