package sopt.org.homepage.faq.service.command.dto;

import lombok.Builder;

import java.util.List;

/**
 * UpdateFAQCommand
 *
 * FAQ 수정 커맨드
 */
@Builder
public record UpdateFAQCommand(
        Long id,
        List<QuestionAnswerCommand> questions
) {
    @Builder
    public record QuestionAnswerCommand(
            String question,
            String answer
    ) {
    }
}