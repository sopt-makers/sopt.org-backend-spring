package sopt.org.homepage.aboutsopt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Core Value 응답")
public record CoreValueResponseDto(
        @Schema(description = "코어밸류 id")
        Long id,

        @Schema(description = "코어밸류", nullable = false)
        String title,

        @Schema(description = "코어밸루 설명", nullable = false)
        String subTitle,

        @Schema(description = "핵심가치 이미지 주소", nullable = false)
        String imageUrl



) {
    @Builder
    public CoreValueResponseDto {}
}
