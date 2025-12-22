package sopt.org.homepage.generation.dto;

import lombok.Builder;
import sopt.org.homepage.generation.Generation;
import sopt.org.homepage.generation.vo.BrandingColor;
import sopt.org.homepage.generation.vo.MainButton;

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
        BrandingColorCommand brandingColor,
        MainButtonCommand mainButton
) {
    public Generation toEntity() {
        return Generation.builder()
                .id(id)
                .name(name)
                .headerImage(headerImage)
                .recruitHeaderImage(recruitHeaderImage)
                .brandingColor(brandingColor.toVO())
                .mainButton(mainButton.toVO())
                .build();
    }

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
