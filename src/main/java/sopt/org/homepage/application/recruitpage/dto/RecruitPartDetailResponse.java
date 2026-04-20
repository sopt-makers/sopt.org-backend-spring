package sopt.org.homepage.application.recruitpage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import sopt.org.homepage.part.dto.PartCurriculumView;
import sopt.org.homepage.part.dto.PartIntroductionView;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;

import java.util.List;

/*
    지원서 페이지 파트 상세 조회 응답 DTO
    파트 소개, 인재상, 커리큘럼
 */
@Builder
public record RecruitPartDetailResponse(
        @Schema(description = "파트 소개")
        String introduction,

        @Schema(description = "선호하는 인재상")
        List<String> preferences,

        @Schema(description = "파트 커리큘럼")
        List<String> partCurriculum
) {
    public static RecruitPartDetailResponse from(
            PartIntroductionView partIntroduction,
            RecruitPartIntroductionView recruitPartIntroduction,
            PartCurriculumView partCurriculum
    ) {
        return RecruitPartDetailResponse.builder()
                .introduction(partIntroduction.description())
                .preferences(parsePreferences(recruitPartIntroduction.introduction().preference()))
                .partCurriculum(partCurriculum.curriculums())
                .build();
    }

    private static List<String> parsePreferences(String preference) {
        return preference.lines()
                .map(line -> line.replaceFirst("^-\\s*", "").trim())
                .filter(line -> !line.isBlank())
                .toList();
    }
}
