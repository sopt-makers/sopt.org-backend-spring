package sopt.org.homepage.generation.dto;

import lombok.Builder;
import sopt.org.homepage.generation.vo.BrandingColor;

/**
 * UpdateGenerationCommand
 * <p>
 * 기수 정보 수정 커맨드
 */
@Builder
public record UpdateGenerationCommand(
        Integer id,
        String name,
        String headerImage,
        String recruitHeaderImage,
        String homeHeaderImage,
        BrandingColorCommand brandingColor
) {
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
