package sopt.org.homepage.recruitment.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.recruitment.domain.RecruitPartIntroduction;

/**
 * RecruitPartIntroductionView
 *
 * 파트 소개 조회 DTO
 */
@Builder
public record RecruitPartIntroductionView(
        Long id,
        Integer generationId,
        String part,  // 레거시 호환용 문자열
        IntroductionView introduction
) {
    public static RecruitPartIntroductionView from(RecruitPartIntroduction recruitPartIntroduction) {
        return RecruitPartIntroductionView.builder()
                .id(recruitPartIntroduction.getId())
                .generationId(recruitPartIntroduction.getGenerationId())
                .part(recruitPartIntroduction.getPart().getValue())
                .introduction(IntroductionView.from(recruitPartIntroduction.getIntroduction()))
                .build();
    }

    @Builder
    public record IntroductionView(
            String content,
            String preference
    ) {
        public static IntroductionView from(sopt.org.homepage.recruitment.domain.vo.PartIntroduction introduction) {
            return IntroductionView.builder()
                    .content(introduction.getContent())
                    .preference(introduction.getPreference())
                    .build();
        }
    }
}