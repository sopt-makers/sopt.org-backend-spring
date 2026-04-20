package sopt.org.homepage.application.recruitpage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import sopt.org.homepage.application.homepage.controller.dto.AboutPageResponse;
import sopt.org.homepage.application.homepage.controller.dto.MainPageResponse;
import sopt.org.homepage.application.homepage.controller.dto.RecruitPageResponse;
import sopt.org.homepage.corevalue.dto.CoreValueView;
import sopt.org.homepage.faq.dto.FAQView;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.part.dto.PartIntroductionView;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;

import java.util.List;

/*
    공식홈페이지가 아닌 리크루팅 지원서 페이지의 메인 페이지 데이터 조회 응답 DTO입니다.
    파트 소개, 핵심 가치, FAQ
 */
@Builder
public record RecruitMainPageResponse(
        @Schema(description = "모집 헤더 이미지 URL")
        String recruitHeaderImage,

        @Schema(description = "파트별 소개")
        List<PartIntroduction> partIntroduction,

        @Schema(description = "핵심 가치")
        List<CoreValue> coreValue,

        @Schema(description = "자주 묻는 질문(FAQ)")
        List<RecruitQuestion> recruitQuestion

) {

    @Builder
    public record PartIntroduction(
            String part,
            String description
    ) {
    }

    @Builder
    public record CoreValue(
            String value,
            String description,
            String image
    ) {
    }

    @Builder
    public record RecruitQuestion(
            String part,
            List<Question> questions
    ) {
        @Builder
        public record Question(
                String question,
                String answer
        ) {
        }
    }

    public static RecruitMainPageResponse from(GenerationDetailView generation, List<CoreValueView> coreValues,
                                               List<PartIntroductionView> partIntroductions,
                                               List<FAQView> faqs){

        return RecruitMainPageResponse.builder()
                .recruitHeaderImage(generation.headerImage())
                .partIntroduction(partIntroductions.stream()
                        .map(pi -> PartIntroduction.builder()
                                .part(pi.part())
                                .description(pi.description())
                                .build())
                        .toList())
                .coreValue(coreValues.stream()
                        .map(cv -> CoreValue.builder()
                                .value(cv.value())
                                .description(cv.description())
                                .image(cv.imageUrl())
                                .build())
                        .toList())
                .recruitQuestion(faqs.stream()
                        .map(f -> RecruitQuestion.builder()
                                .part(f.part().getValue())
                                .questions(f.questions().stream()
                                        .map(qav -> RecruitQuestion.Question.builder()
                                                .question(qav.question())
                                                .answer(qav.answer())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }
}
