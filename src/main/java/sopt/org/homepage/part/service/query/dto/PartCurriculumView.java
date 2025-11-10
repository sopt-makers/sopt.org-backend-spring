package sopt.org.homepage.part.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.part.domain.Part;

import java.util.List;

/**
 * PartCurriculumView
 *
 * 파트 커리큘럼 조회 DTO (About 페이지용)
 */
@Builder
public record PartCurriculumView(
        Long id,
        String part,
        List<String> curriculums
) {
    public static PartCurriculumView from(Part part) {
        return PartCurriculumView.builder()
                .id(part.getId())
                .part(part.getPartType().getValue())
                .curriculums(part.getCurriculums())
                .build();
    }
}