package sopt.org.homepage.faq.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.faq.FAQ;
import sopt.org.homepage.faq.vo.FAQCategory;

@Builder
public record FAQView(
        Long id,
        FAQCategory category,
        List<QuestionAnswerView> questions
) {
    public static FAQView from(FAQ faq) {
        return FAQView.builder()
                .id(faq.getId())
                .category(faq.getCategory())
                .questions(faq.getQuestions().stream()
                        .map(qa -> new QuestionAnswerView(qa.question(), qa.answer()))
                        .toList())
                .build();
    }

    @Builder
    public record QuestionAnswerView(String question, String answer) {
    }
}
