package sopt.org.homepage.aboutsopt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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
        String imageUrl,

        @Schema(description = "생성일자", nullable = false, example = "2024-12-16T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일자", nullable = false, example = "2024-12-16T15:00:00")
        LocalDateTime updatedAt

) {
    @Builder
    public CoreValueResponseDto {}
}
