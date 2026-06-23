package sopt.org.homepage.generation.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import sopt.org.homepage.generation.Generation;
import sopt.org.homepage.generation.vo.BrandingColor;

/**
 * GenerationDetailView
 * <p>
 * 기수 상세 조회 DTO
 */
@Builder
public record GenerationDetailView(
        Integer id,
        String name,
        String headerImage,
        String recruitHeaderImage,
        String homeHeaderImage,
        BrandingColorView brandingColor,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GenerationDetailView from(Generation generation) {
        return GenerationDetailView.builder()
                .id(generation.getId())
                .name(generation.getName())
                .headerImage(generation.getHeaderImage())
                .recruitHeaderImage(generation.getRecruitHeaderImage())
                .homeHeaderImage(generation.getHomeHeaderImage())
                .brandingColor(BrandingColorView.from(generation.getBrandingColor()))
                .createdAt(generation.getCreatedAt())
                .updatedAt(generation.getUpdatedAt())
                .build();
    }

    @Builder
    public record BrandingColorView(
            String darkModeKeyColor,
            String darkModeTextColor,
            String lightModeKeyColor,
            String lightModeTextColor
    ) {
        public static BrandingColorView from(BrandingColor brandingColor) {
            return BrandingColorView.builder()
                    .darkModeKeyColor(brandingColor.getDarkModeKeyColor())
                    .darkModeTextColor(brandingColor.getDarkModeTextColor())
                    .lightModeKeyColor(brandingColor.getLightModeKeyColor())
                    .lightModeTextColor(brandingColor.getLightModeTextColor())
                    .build();
        }
    }

}
