package sopt.org.homepage.admin.dto.response.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "핵심가치 이미지 S3 PresigneUrl 정보")
@Builder
public record AddMainCoreValueResponseRecordDto(
        @Schema(description = "핵심 가치", example = "용기", requiredMode = Schema.RequiredMode.REQUIRED) String value,
        @Schema(description = "핵심가치 이미지 PresgiendUrl", requiredMode = Schema.RequiredMode.REQUIRED) String image) {
}

