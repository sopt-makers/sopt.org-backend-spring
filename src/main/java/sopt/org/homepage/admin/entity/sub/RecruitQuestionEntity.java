package sopt.org.homepage.admin.entity.sub;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitQuestionEntity {
    private String part;
    private List<QuestionEntity> questions;
}

