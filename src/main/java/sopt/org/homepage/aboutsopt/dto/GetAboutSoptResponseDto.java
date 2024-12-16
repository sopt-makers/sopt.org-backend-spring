package sopt.org.homepage.aboutsopt.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "AboutSopt 응답")
public record GetAboutSoptResponseDto(
        @Schema(description = "AboutSopt 정보")
        AboutSoptResponseDto aboutSopt,

        @Schema(description = "활동 기록")
        ActivitiesRecords activitiesRecords
) {
    @Builder
    public GetAboutSoptResponseDto {}

    public record ActivitiesRecords(
            @Schema(description = "활동 멤버 수")
            int activitiesMemberCount,

            @Schema(description = "프로젝트 수")
            int projectCounts,

            @Schema(description = "스터디 수")
            Integer studyCounts
    ) {
        @Builder
        public ActivitiesRecords {}
    }
}