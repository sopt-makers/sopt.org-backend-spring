package sopt.org.homepage.part.service.command.dto;

import lombok.Builder;

import java.util.List;

/**
 * BulkCreatePartsCommand
 *
 * 파트 일괄 생성 커맨드 (Admin용)
 */
@Builder
public record BulkCreatePartsCommand(
        Integer generationId,
        List<PartData> partIntroductions,    // Main 페이지용
        List<PartCurriculumData> partCurriculums  // About 페이지용
) {
    @Builder
    public record PartData(
            String part,  // 레거시 문자열
            String description
    ) {
    }

    @Builder
    public record PartCurriculumData(
            String part,  // 레거시 문자열
            List<String> curriculums
    ) {
    }
}