package sopt.org.homepage.generation.dto;

import lombok.Builder;
import sopt.org.homepage.generation.Generation;
import sopt.org.homepage.generation.vo.BrandingColor;

/**
 * CreateGenerationCommand
 * <p>
 * 새로운 기수 생성 커맨드
 */
@Builder
public record CreateGenerationCommand(
        Integer id,
        String name,
        String headerImage,
        String recruitHeaderImage,
        String homeHeaderImage,
        BrandingColorCommand brandingColor
) {
    public Generation toEntity() {
        return Generation.builder()
                .id(id)
                .name(name)
                .headerImage(headerImage)
                .recruitHeaderImage(recruitHeaderImage)
                .homeHeaderImage(homeHeaderImage)
                .brandingColor(brandingColor.toVO())
                .build();
    }

    @Builder
    public record BrandingColorCommand(
            String darkModeKeyColor,
            String darkModeTextColor,
            String lightModeKeyColor,
            String lightModeTextColor
    ) {
        public BrandingColor toVO() {
            return BrandingColor.builder()
                    .darkModeKeyColor(darkModeKeyColor)
                    .darkModeTextColor(darkModeTextColor)
                    .lightModeKeyColor(lightModeKeyColor)
                    .lightModeTextColor(lightModeTextColor)
                    .build();
        }
    }

}
