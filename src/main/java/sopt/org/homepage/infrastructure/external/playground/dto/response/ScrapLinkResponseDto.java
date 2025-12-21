package sopt.org.homepage.infrastructure.external.playground.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "블로그 스크랩 정보 응답")
public class ScrapLinkResponseDto {
    @Schema(description = "블로그 스크랩 썸네일 이미지", requiredMode = Schema.RequiredMode.REQUIRED)
    private String thumbnailUrl;

    @Schema(description = "블로그 스크랩 제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "블로그 스크랩 설명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "블로그 스크랩 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;
}
