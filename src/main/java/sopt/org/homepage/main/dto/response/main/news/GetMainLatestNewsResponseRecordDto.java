package sopt.org.homepage.main.dto.response.main.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "최신소식")
@Builder
public record GetMainLatestNewsResponseRecordDto(
        @Schema(description = "최신소식 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED) int id,
        @Schema(description = "최신소식 제목", example = "Mind 23", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(description = "최신소식 이미지 링크", example = "https://image.url", requiredMode = Schema.RequiredMode.REQUIRED) String image,
        @Schema(description = "최신소식 링크", example = "https://news.url", requiredMode = Schema.RequiredMode.REQUIRED) String link
) {
}
