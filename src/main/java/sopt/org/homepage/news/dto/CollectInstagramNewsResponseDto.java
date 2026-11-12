package sopt.org.homepage.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인스타 뉴스 수집 시작 응답")
public record CollectInstagramNewsResponseDto(
        @Schema(description = "Apify 수집 작업 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        String runId
) {
}
