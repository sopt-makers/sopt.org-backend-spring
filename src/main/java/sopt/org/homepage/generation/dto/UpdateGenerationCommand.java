package sopt.org.homepage.generation.dto;

import lombok.Builder;
import sopt.org.homepage.generation.vo.BrandingColor;
import sopt.org.homepage.generation.vo.MainButton;

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
        BrandingColorCommand brandingColor,
        MainButtonCommand mainButton
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

    @Builder
    public record MainButtonCommand(
            String text,
            String keyColor,
            String subColor
    ) {
        public MainButton toVO() {
            return MainButton.builder()
                    .text(text)
                    .keyColor(keyColor)
                    .subColor(subColor)
                    .build();
        }
    }
}
