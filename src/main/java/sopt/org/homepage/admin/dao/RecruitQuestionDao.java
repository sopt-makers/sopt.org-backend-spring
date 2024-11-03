package sopt.org.homepage.admin.dao;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitQuestionDao {
    private String part;
    private List<QuestionDao> questions;
}

