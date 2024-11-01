package sopt.org.homepage.admin.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecruitQuestionDao {
    private String part;
    private String questions;
    private String description;
    private Boolean required;
}
