package sopt.org.homepage.faq.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.faq.FAQ;
import sopt.org.homepage.faq.vo.FAQCategory;

@Builder
public record CreateFAQCommand(
        FAQCategory category,
        List<QuestionAnswerCommand> questions
) {
    public FAQ toEntity() {
        List<FAQ.QuestionAnswer> questionAnswers = questions != null
                ? questions.stream()
                .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                .toList()
                : List.of();
        return FAQ.builder().category(category).questions(questionAnswers).build();
    }

    @Builder
    public record QuestionAnswerCommand(String question, String answer) {
    }
}
