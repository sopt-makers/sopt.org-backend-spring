package sopt.org.homepage.part.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.part.domain.Part;

/**
 * PartIntroductionView
 *
 * 파트 소개 조회 DTO (Main 페이지용)
 */
@Builder
public record PartIntroductionView(
        Long id,
        String part,
        String description
) {
    public static PartIntroductionView from(Part part) {
        return PartIntroductionView.builder()
                .id(part.getId())
                .part(part.getPartType().getValue())
                .description(part.getDescription())
                .build();
    }
}