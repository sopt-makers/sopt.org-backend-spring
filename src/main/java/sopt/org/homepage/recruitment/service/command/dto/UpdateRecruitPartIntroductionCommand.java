package sopt.org.homepage.recruitment.service.command.dto;

import lombok.Builder;
import sopt.org.homepage.recruitment.domain.vo.PartIntroduction;

/**
 * UpdateRecruitPartIntroductionCommand
 *
 * 파트 소개 수정 커맨드
 */
@Builder
public record UpdateRecruitPartIntroductionCommand(
        Long id,
        IntroductionCommand introduction
) {
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