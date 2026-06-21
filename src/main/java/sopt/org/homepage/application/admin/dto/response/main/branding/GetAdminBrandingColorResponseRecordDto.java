package sopt.org.homepage.application.admin.dto.response.main.branding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "브랜딩 컬러 정보")
@Builder
public record GetAdminBrandingColorResponseRecordDto(
        @Schema(description = "다크모드 키 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED) String darkModeKeyColor,
        @Schema(description = "다크모드 텍스트 컬러", example = "white", requiredMode = Schema.RequiredMode.REQUIRED) String darkModeTextColor,
        @Schema(description = "라이트모드 키 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED) String lightModeKeyColor,
        @Schema(description = "라이트모드 텍스트 컬러", example = "black", requiredMode = Schema.RequiredMode.REQUIRED) String lightModeTextColor
) {
}
