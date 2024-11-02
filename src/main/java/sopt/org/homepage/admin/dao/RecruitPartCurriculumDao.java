package sopt.org.homepage.admin.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecruitPartCurriculumDao {
    private String part;
    private List<Introduction> introduction;
}

@Getter
@Setter
@NoArgsConstructor
class Introduction {
    private String content;
    private String preference;
}

