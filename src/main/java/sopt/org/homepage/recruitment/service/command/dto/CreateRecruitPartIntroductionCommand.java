package sopt.org.homepage.recruitment.service.command.dto;

import lombok.Builder;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.recruitment.domain.RecruitPartIntroduction;
import sopt.org.homepage.recruitment.domain.vo.PartIntroduction;

/**
 * CreateRecruitPartIntroductionCommand
 *
 * 파트 소개 생성 커맨드
 */
@Builder
public record CreateRecruitPartIntroductionCommand(
        Integer generationId,
        PartType part,
        IntroductionCommand introduction
) {
    public RecruitPartIntroduction toEntity() {
        return RecruitPartIntroduction.builder()
                .generationId(generationId)
                .part(part)
                .introduction(introduction.toVO())
                .build();
    }

    @Builder
    public record IntroductionCommand(
            String content,
            String preference
    ) {
        public PartIntroduction toVO() {
            return PartIntroduction.builder()
                    .content(content)
                    .preference(preference)
                    .build();
        }
    }
}