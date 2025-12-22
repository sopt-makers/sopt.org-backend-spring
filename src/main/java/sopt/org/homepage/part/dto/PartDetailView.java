package sopt.org.homepage.part.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.part.Part;

/**
 * PartDetailView
 * <p>
 * 파트 상세 조회 DTO
 */
@Builder
public record PartDetailView(
        Long id,
        Integer generationId,
        String part,  // 레거시 호환용 문자열
        String description,
        List<String> curriculums
) {
    public static PartDetailView from(Part part) {
        return PartDetailView.builder()
                .id(part.getId())
                .generationId(part.getGenerationId())
                .part(part.getPartType().getValue())  // Enum → 한글 문자열
                .description(part.getDescription())
                .curriculums(part.getCurriculums())
                .build();
    }
}
