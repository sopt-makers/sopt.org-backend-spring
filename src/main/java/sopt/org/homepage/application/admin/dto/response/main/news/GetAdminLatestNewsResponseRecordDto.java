package sopt.org.homepage.application.admin.dto.response.main.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "최신소식")
@Builder
public record GetAdminLatestNewsResponseRecordDto(
        @Schema(description = "최신소식 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED) int id,
        @Schema(description = "최신소식 제목", example = "Mind 23", requiredMode = Schema.RequiredMode.REQUIRED) String title
) {
}
