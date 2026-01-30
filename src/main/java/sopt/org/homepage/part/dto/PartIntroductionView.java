package sopt.org.homepage.part.dto;

import lombok.Builder;
import sopt.org.homepage.part.Part;

/**
 * PartIntroductionView
 * <p>
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
