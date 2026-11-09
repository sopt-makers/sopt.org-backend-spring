package sopt.org.homepage.application.homepage.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import sopt.org.homepage.application.homepage.review.domain.HomepageReviewType;

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

        @Schema(description = "홈 헤더 이미지")
        String homeHeaderImage,

        BrandingColor brandingColor,
        List<CoreValue> coreValue,
        List<PartIntroduction> partIntroduction,
        List<LatestNews> latestNews,
        List<RecruitSchedule> recruitSchedule,
        ActivitiesRecords activitiesRecords,
        List<Review> reviews,

        @Schema(description = "카테고리별 자주 묻는 질문 목록")
        List<FAQ> faq
) {

    @Builder
    public record BrandingColor(
            String darkModeKeyColor,
            String darkModeTextColor,
            String lightModeKeyColor,
            String lightModeTextColor
    ) {
    }

    @Builder
    public record CoreValue(
            String value,
            String description,
            String detailDescription
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
            String link,
            String publishedDate
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
            int studyCounts,

            @Schema(description = "전체 기수 창업 중인 프로젝트 수", example = "10")
            int foundingTeamCount,

            @Schema(description = "전체 기수 이용 가능한 프로젝트 수", example = "20")
            int availableServiceCount,

            @Schema(description = "운영 기간", example = "37")
            int operationPeriod
    ) {
    }

    @Builder
    public record Review(
            @Schema(description = "ID", example = "1")
            int id,

            @Schema(description = "후기 종류 (ACTIVITY: 활동 후기, RECRUITING: 지원 후기)", example = "ACTIVITY")
            HomepageReviewType type,

            @Schema(description = "제목", example = "후회없는 활동")
            String title,

            @Schema(description = "내용", example = "후회없는 활동이었어요")
            String content,

            @Schema(description = "작성자 정보", example = "김솝트 | 36, 37기 활동 | 서버")
            String authorInfo
    ){
    }

    @Builder
    public record FAQ(
            @Schema(description = "FAQ 카테고리", example = "지원자격", allowableValues = {"지원자격", "면접", "활동"})
            String category,

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
