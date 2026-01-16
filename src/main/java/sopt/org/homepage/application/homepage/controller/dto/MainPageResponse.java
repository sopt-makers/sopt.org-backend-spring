package sopt.org.homepage.application.homepage.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

/**
 * MainPageResponse
 * <p>
 * GET /homepage 응답 DTO
 */
@Schema(description = "메인 페이지 데이터")
@Builder
public record MainPageResponse(
        @Schema(description = "기수", example = "35")
        int generation,

        @Schema(description = "기수명", example = "35기")
        String name,

        BrandingColor brandingColor,
        MainButton mainButton,
        List<PartIntroduction> partIntroduction,
        List<LatestNews> latestNews,
        List<RecruitSchedule> recruitSchedule,
        ActivitiesRecords activitiesRecords // ✅ 추가
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
    public record MainButton(
            String text,
            String keyColor,
            String subColor
    ) {
    }

    @Builder
    public record PartIntroduction(
            String part,
            String description
    ) {
    }

    @Builder
    public record LatestNews(
            int id,
            String title,
            String image,
            String link
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

    // ✅ 신규 추가
    @Builder
    public record ActivitiesRecords(
            @Schema(description = "활동 회원 수", example = "154")
            int activitiesMemberCount,

            @Schema(description = "프로젝트 수", example = "1")
            int projectCounts,

            @Schema(description = "스터디 수", example = "98")
            int studyCounts
    ) {
    }
}
