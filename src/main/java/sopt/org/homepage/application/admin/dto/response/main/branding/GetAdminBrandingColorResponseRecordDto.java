package sopt.org.homepage.application.admin.dto.response.main.branding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "브랜딩 컬러 정보")
@Builder
public record GetAdminBrandingColorResponseRecordDto(
        @Schema(description = "메인 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED) String main,
        @Schema(description = "로우 톤 컬러", example = "#CC0000", requiredMode = Schema.RequiredMode.REQUIRED) String low,
        @Schema(description = "하이 톤 컬러", example = "#FF3333", requiredMode = Schema.RequiredMode.REQUIRED) String high,
        @Schema(description = "포인트 컬러", example = "#FF9999", requiredMode = Schema.RequiredMode.REQUIRED) String point
) {
}
