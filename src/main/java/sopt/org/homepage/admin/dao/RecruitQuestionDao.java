package sopt.org.homepage.admin.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecruitQuestionDao {
    private String part;
    private List<Question> questions;
}

@Getter
@Setter
@NoArgsConstructor
class Question {
    private String question;
    private String answer;
}
