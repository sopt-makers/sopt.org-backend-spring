package sopt.org.homepage.faq.service.command.dto;

import lombok.Builder;

import java.util.List;

/**
 * BulkCreateFAQsCommand
 *
 * FAQ 일괄 생성 커맨드 (Admin용)
 */
@Builder
public record BulkCreateFAQsCommand(
        List<FAQData> faqs
) {
    @Builder
    public record FAQData(
            String part,  // 레거시 문자열
            List<QuestionData> question  // 레거시 필드명
    ) {
    }

    @Builder
    public record QuestionData(
            String question,
            String answer
    ) {
    }
}