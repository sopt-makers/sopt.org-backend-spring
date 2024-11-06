package sopt.org.homepage.admin.dto.response.main.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "핵심 가치 정보")
@Builder
public record GetMainCoreValueResponseRecordDto(
        @Schema(description = "핵심 가치", example = "용기", requiredMode = Schema.RequiredMode.REQUIRED) String value,
        @Schema(description = "핵심 가치 설명", example = "새로운 도전을 위해 과감히 용기내는 사람", requiredMode = Schema.RequiredMode.REQUIRED) String description,
        @Schema(description = "핵심 가치 이미지 링크", example = "https://corevalue.png", requiredMode = Schema.RequiredMode.REQUIRED) String image
) {
}
