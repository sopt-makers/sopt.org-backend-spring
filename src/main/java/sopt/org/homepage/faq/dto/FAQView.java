package sopt.org.homepage.faq.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.faq.FAQ;
import sopt.org.homepage.global.common.type.PartType;

@Builder
public record FAQView(
        Long id,
        PartType part,
        List<QuestionAnswerView> questions
) {
    public static FAQView from(FAQ faq) {
        return FAQView.builder()
                .id(faq.getId())
                .part(faq.getPart())
                .questions(faq.getQuestions().stream()
                        .map(qa -> new QuestionAnswerView(qa.question(), qa.answer()))
                        .toList())
                .build();
    }

    @Builder
    public record QuestionAnswerView(String question, String answer) {
    }
}
