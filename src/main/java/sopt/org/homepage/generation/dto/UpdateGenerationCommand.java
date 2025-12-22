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
        BrandingColorCommand brandingColor,
        MainButtonCommand mainButton
) {
    @Builder
    public record BrandingColorCommand(
            String main,
            String sub,
            String point,
            String background
    ) {
        public BrandingColor toVO() {
            return BrandingColor.builder()
                    .main(main)
                    .sub(sub)
                    .point(point)
                    .background(background)
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
