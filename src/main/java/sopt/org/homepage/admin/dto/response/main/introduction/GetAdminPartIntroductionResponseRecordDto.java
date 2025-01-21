package sopt.org.homepage.admin.dto.response.main.introduction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "파트 소개 정보")
@Builder
public record GetAdminPartIntroductionResponseRecordDto(
        @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED) String part,
        @Schema(description = "파트 설명", example = "Android 앱 개발", requiredMode = Schema.RequiredMode.REQUIRED) String description
) {
}
