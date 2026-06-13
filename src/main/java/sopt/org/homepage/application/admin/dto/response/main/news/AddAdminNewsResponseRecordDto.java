package sopt.org.homepage.application.admin.dto.response.main.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "최신소식 이미지 S3 PresignedUrl 정보")
@Builder
public record AddAdminNewsResponseRecordDto(
        @Schema(description = "뉴스 제목", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(description = "뉴스 이미지 PresignedUrl", requiredMode = Schema.RequiredMode.REQUIRED) String imagePresignedUrl
) {
}
