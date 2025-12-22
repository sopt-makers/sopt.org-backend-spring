package sopt.org.homepage.faq.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record BulkCreateFAQsCommand(List<FAQData> faqs) {

    @Builder
    public record FAQData(String part, List<QuestionData> question) {
    }

    @Builder
    public record QuestionData(String question, String answer) {
    }
}
