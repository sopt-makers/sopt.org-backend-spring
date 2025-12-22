package sopt.org.homepage.part.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.part.domain.Part;

import java.util.List;

/**
 * PartDetailView
 *
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