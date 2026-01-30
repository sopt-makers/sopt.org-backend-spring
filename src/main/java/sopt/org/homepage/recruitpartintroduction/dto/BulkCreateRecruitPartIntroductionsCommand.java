package sopt.org.homepage.recruitpartintroduction.dto;

import java.util.List;
import lombok.Builder;

/**
 * BulkCreateRecruitPartIntroductionsCommand
 * <p>
 * 파트 소개 일괄 생성 커맨드 (Admin용)
 */
@Builder
public record BulkCreateRecruitPartIntroductionsCommand(
        Integer generationId,
        List<PartIntroductionData> partIntroductions
) {
    @Builder
    public record PartIntroductionData(
            String part,  // 레거시 문자열
            IntroductionData introduction
    ) {
    }

    @Builder
    public record IntroductionData(
            String content,
            String preference
    ) {
    }
}
