package sopt.org.homepage.generation.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import sopt.org.homepage.generation.Generation;
import sopt.org.homepage.generation.vo.BrandingColor;
import sopt.org.homepage.generation.vo.MainButton;

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
        BrandingColorView brandingColor,
        MainButtonView mainButton,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GenerationDetailView from(Generation generation) {
        return GenerationDetailView.builder()
                .id(generation.getId())
                .name(generation.getName())
                .headerImage(generation.getHeaderImage())
                .recruitHeaderImage(generation.getRecruitHeaderImage())
                .brandingColor(BrandingColorView.from(generation.getBrandingColor()))
                .mainButton(MainButtonView.from(generation.getMainButton()))
                .createdAt(generation.getCreatedAt())
                .updatedAt(generation.getUpdatedAt())
                .build();
    }

    @Builder
    public record BrandingColorView(
            String main,
            String sub,
            String point,
            String background,
            // 레거시 호환용
            String low,
            String high
    ) {
        public static BrandingColorView from(BrandingColor brandingColor) {
            return BrandingColorView.builder()
                    .main(brandingColor.getMain())
                    .sub(brandingColor.getSub())
                    .point(brandingColor.getPoint())
                    .background(brandingColor.getBackground())
                    .low(brandingColor.getLow())    // sub와 동일
                    .high(brandingColor.getHigh())  // point와 동일
                    .build();
        }
    }

    @Builder
    public record MainButtonView(
            String text,
            String keyColor,
            String subColor
    ) {
        public static MainButtonView from(MainButton mainButton) {
            return MainButtonView.builder()
                    .text(mainButton.getText())
                    .keyColor(mainButton.getKeyColor())
                    .subColor(mainButton.getSubColor())
                    .build();
        }
    }
}
