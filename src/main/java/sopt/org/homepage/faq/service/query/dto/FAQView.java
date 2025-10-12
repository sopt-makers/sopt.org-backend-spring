package sopt.org.homepage.faq.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.faq.domain.FAQ;

import java.util.List;

/**
 * FAQView
 *
 * FAQ 조회 DTO
 */
@Builder
public record FAQView(
        Long id,
        String part,  // 레거시 호환용 문자열
        List<QuestionAnswerView> questions
) {
    public static FAQView from(FAQ faq) {
        List<QuestionAnswerView> questions = faq.getQuestions().stream()
                .map(qa -> new QuestionAnswerView(qa.question(), qa.answer()))
                .toList();

        return FAQView.builder()
                .id(faq.getId())
                .part(faq.getPart().getValue())
                .questions(questions)
                .build();
    }

    /**
     * 레거시 API 호환용 (question 필드명)
     */
    public List<QuestionAnswerView> getQuestion() {
        return this.questions;
    }

    public record QuestionAnswerView(
            String question,
            String answer
    ) {
    }
}