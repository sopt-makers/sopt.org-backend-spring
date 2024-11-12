package sopt.org.homepage.admin.dto.response.main.button;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "메인 버튼 스타일")
@Builder
public record GetAdminMainButtonResponseRecordDto(
        @Schema(description = "버튼 텍스트", example = "지원하기", requiredMode = Schema.RequiredMode.REQUIRED) String text,
        @Schema(description = "주요 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED) String keyColor,
        @Schema(description = "보조 컬러", example = "#CC0000", requiredMode = Schema.RequiredMode.REQUIRED) String subColor
) {
}
