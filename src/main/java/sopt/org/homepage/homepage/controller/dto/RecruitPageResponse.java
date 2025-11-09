package sopt.org.homepage.homepage.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * RecruitPageResponse
 *
 * GET /homepage/recruit 응답 DTO
 */
@Schema(description = "Recruiting 페이지 데이터")
@Builder
public record RecruitPageResponse(
        @Schema(description = "기수", example = "35")
        int generation,

        @Schema(description = "기수명", example = "35기")
        String name,

        @Schema(description = "모집 헤더 이미지 URL")
        String recruitHeaderImage,

        BrandingColor brandingColor,
        List<RecruitSchedule> recruitSchedule,
        List<RecruitPartCurriculum> recruitPartCurriculum,
        List<RecruitQuestion> recruitQuestion
) {
    @Builder
    public record BrandingColor(
            String main,
            String high,
            String low,
            String point
    ) {
    }

    @Builder
    public record RecruitSchedule(
            String type,
            Schedule schedule
    ) {
        @Builder
        public record Schedule(
                String applicationStartTime,
                String applicationEndTime,
                String applicationResultTime,
                String interviewStartTime,
                String interviewEndTime,
                String finalResultTime
        ) {
        }
    }

    @Builder
    public record RecruitPartCurriculum(
            String part,
            Introduction introduction
    ) {
        @Builder
        public record Introduction(
                String content,
                String preference
        ) {
        }
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
}
