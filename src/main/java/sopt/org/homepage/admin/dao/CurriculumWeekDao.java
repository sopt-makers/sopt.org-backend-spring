package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumWeekDao {
    private Integer week;          
    private String description;    
}
