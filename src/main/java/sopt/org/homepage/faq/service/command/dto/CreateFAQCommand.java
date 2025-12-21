package sopt.org.homepage.faq.service.command.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.faq.domain.FAQ;
import sopt.org.homepage.global.common.type.PartType;

/**
 * CreateFAQCommand
 * <p>
 * FAQ 생성 커맨드
 */
@Builder
public record CreateFAQCommand(
        PartType part,
        List<QuestionAnswerCommand> questions
) {
    public FAQ toEntity() {
        List<FAQ.QuestionAnswer> questionAnswers = questions != null
                ? questions.stream()
                .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                .toList()
                : List.of();

        return FAQ.builder()
                .part(part)
                .questions(questionAnswers)
                .build();
    }

    @Builder
    public record QuestionAnswerCommand(
            String question,
            String answer
    ) {
    }
}
