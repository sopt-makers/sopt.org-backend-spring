package sopt.org.homepage.part.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.part.Part;

/**
 * PartCurriculumView
 * <p>
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
